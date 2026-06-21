package com.focusshield.app.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.focusshield.app.domain.repository.TimerStateRepository
import com.focusshield.app.service.foreground.FocusForegroundService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var timerStateRepository: TimerStateRepository

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            receiverScope.launch {
                val state = timerStateRepository.getTimerState()
                if (state != null && state.isRunning && state.remainingMillis > 0) {
                    // Resume the timer service
                    val resumeIntent = Intent(context, FocusForegroundService::class.java).apply {
                        action = FocusForegroundService.ACTION_START_TIMER
                        putExtra(FocusForegroundService.EXTRA_DURATION_MINS, (state.remainingMillis / 60000).toInt())
                    }
                    context.startForegroundService(resumeIntent)
                }
            }
        }
    }
}
