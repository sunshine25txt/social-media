package com.focusshield.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.focusshield.app.data.local.dao.WebsiteRuleDao
import com.focusshield.app.data.local.entity.BlockedEventEntity
import com.focusshield.app.domain.repository.AppRuleRepository
import com.focusshield.app.domain.repository.BlockedEventRepository
import com.focusshield.app.domain.repository.AppLockRepository
import com.focusshield.app.domain.repository.PauseRepository
import com.focusshield.app.domain.repository.TimerStateRepository
import com.focusshield.app.ui.applock.AppLockActivity
import com.focusshield.app.ui.applock.AppLockManager
import com.focusshield.app.ui.overlay.BlockOverlayActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusShieldAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var appRuleRepository: AppRuleRepository

    @Inject
    lateinit var blockedEventRepository: BlockedEventRepository

    @Inject
    lateinit var pauseRepository: PauseRepository

    @Inject
    lateinit var appLockRepository: AppLockRepository

    @Inject
    lateinit var websiteRuleDao: WebsiteRuleDao

    @Inject
    lateinit var timerStateRepository: TimerStateRepository

    @Inject
    lateinit var selectorRepository: SelectorRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var genericDetector: GenericAppDetector
    private lateinit var tikTokDetector: TikTokDetector
    private lateinit var snackVideoDetector: SnackVideoDetector
    private val browserPackages = listOf(
        "com.android.chrome",
        "org.mozilla.firefox",
        "com.sec.android.app.sbrowser",
        "com.opera.browser",
        "com.microsoft.emmx"
    )

    override fun onCreate() {
        super.onCreate()
        genericDetector = GenericAppDetector(selectorRepository)
        tikTokDetector = TikTokDetector()
        snackVideoDetector = SnackVideoDetector()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        val eventType = event.eventType
        if (eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && 
            eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return
        
        // Skip self
        if (packageName == this.packageName) return

        val rootNode = rootInActiveWindow ?: return

        serviceScope.launch {
            // 1. Check App Lock status
            val appLock = appLockRepository.getLockForPackage(packageName)
            if (appLock != null && appLock.isLocked && !AppLockManager.isPackageUnlocked(packageName)) {
                val intent = Intent(this@FocusShieldAccessibilityService, AppLockActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("BLOCKED_PACKAGE", packageName)
                }
                startActivity(intent)
                performGlobalAction(GLOBAL_ACTION_HOME)
                return@launch
            }

            // 2. Check if app is monitored
            val rule = appRuleRepository.getRuleForPackage(packageName)
            val isBrowser = browserPackages.contains(packageName)
            
            if (rule != null && rule.isMonitored) {
                // Monitored App
                if (isPauseOrAllowanceActive(packageName)) {
                    // Allowed due to pause or allowance timer
                    return@launch
                }

                // Choose detector
                val detector = when (packageName) {
                    "com.zhiliaoapp.musically" -> tikTokDetector
                    "com.kwai.video" -> snackVideoDetector
                    else -> genericDetector
                }

                val result = detector.detect(rootNode, packageName)
                if (result is DetectionResult.Blocked) {
                    triggerBlock(result)
                }
            } else if (isBrowser) {
                // Browser URL checking
                val blockedDomains = websiteRuleDao.getBlockedWebsites().firstOrNull()?.map { it.domainPattern } ?: emptyList()
                if (blockedDomains.isNotEmpty()) {
                    val browserDetector = BrowserDetector(blockedDomains)
                    val result = browserDetector.detect(rootNode, packageName)
                    if (result is DetectionResult.Blocked) {
                        triggerBlock(result)
                    }
                }
            }
        }
    }

    private suspend fun isPauseOrAllowanceActive(packageName: String): Boolean {
        val now = System.currentTimeMillis()
        
        // Check active pauses
        val recentPauses = pauseRepository.getPausesToday(now - 24 * 60 * 60 * 1000).first()
        val hasActivePause = recentPauses.any { pause ->
            val elapsed = now - pause.timestamp
            val durationMillis = pause.durationMinutes * 60 * 1000
            elapsed < durationMillis && (pause.appPackage == null || pause.appPackage == packageName)
        }
        if (hasActivePause) return true

        // Check active watch allowance timer
        val timerState = timerStateRepository.getTimerState()
        if (timerState != null && timerState.isRunning && timerState.associatedApp == packageName) {
            val elapsedSinceUpdate = now - timerState.lastUpdatedMillis
            val remaining = timerState.remainingMillis - elapsedSinceUpdate
            if (remaining > 0) {
                return true
            }
        }

        return false
    }

    private fun triggerBlock(blocked: DetectionResult.Blocked) {
        serviceScope.launch {
            // 1. Log block event in database
            blockedEventRepository.insert(
                BlockedEventEntity(
                    appPackage = blocked.appPackage,
                    surfaceType = blocked.surfaceType,
                    timestampMillis = System.currentTimeMillis(),
                    estimatedSecondsSaved = 30 // Estimate 30 seconds saved per block warning
                )
            )

            // 2. Launch Block Overlay Activity
            val intent = Intent(this@FocusShieldAccessibilityService, BlockOverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("BLOCKED_PACKAGE", blocked.appPackage)
                putExtra("BLOCKED_SURFACE", blocked.surfaceType)
                putExtra("BLOCKED_REASON", blocked.reason)
            }
            startActivity(intent)

            // 3. Kick user out of loops back to home screen
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    override fun onInterrupt() {
        // Handle interruptions
    }
}
