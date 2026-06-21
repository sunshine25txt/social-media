package com.focusshield.app.domain.usecase

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.focusshield.app.service.accessibility.FocusShieldAccessibilityService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class PermissionStates(
    val hasAccessibility: Boolean,
    val hasUsageStats: Boolean,
    val hasOverlay: Boolean,
    val hasNotifications: Boolean
) {
    val allGranted: Boolean
        get() = hasAccessibility && hasUsageStats && hasOverlay && hasNotifications
}

@Singleton
class CheckPermissionStatusUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun execute(): PermissionStates {
        return PermissionStates(
            hasAccessibility = isAccessibilityEnabled(),
            hasUsageStats = isUsageStatsEnabled(),
            hasOverlay = isOverlayEnabled(),
            hasNotifications = isNotificationsEnabled()
        )
    }

    fun isAccessibilityEnabled(): Boolean {
        val serviceName = ComponentName(context, FocusShieldAccessibilityService::class.java).flattenToShortString()
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(serviceName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun isUsageStatsEnabled(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun isOverlayEnabled(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}
