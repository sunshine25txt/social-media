package com.focusshield.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FocusShieldApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannels() {
        val focusChannel = NotificationChannel(
            CHANNEL_FOCUS_SERVICE,
            getString(R.string.notification_channel_focus),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Persistent notification while Focus Shield is actively monitoring"
            setShowBadge(false)
        }

        val alertChannel = NotificationChannel(
            CHANNEL_ALERTS,
            getString(R.string.notification_channel_alerts),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Important alerts about permissions and focus streaks"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannels(listOf(focusChannel, alertChannel))
    }

    companion object {
        const val CHANNEL_FOCUS_SERVICE = "focus_service"
        const val CHANNEL_ALERTS = "focus_alerts"
    }
}
