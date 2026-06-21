package com.focusshield.app.service.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.focusshield.app.domain.repository.TimerStateRepository
import com.focusshield.app.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusForegroundService : Service() {

    @Inject
    lateinit var timerStateRepository: TimerStateRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isTimerRunning = false

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "focus_foreground_channel"
        const val ACTION_START_TIMER = "ACTION_START_TIMER"
        const val ACTION_STOP_TIMER = "ACTION_STOP_TIMER"
        const val EXTRA_DURATION_MINS = "EXTRA_DURATION_MINS"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val durationMins = intent.getIntExtra(EXTRA_DURATION_MINS, 5)
                startForeground(NOTIFICATION_ID, buildNotification("Timer starting..."))
                startTimer(durationMins)
            }
            ACTION_STOP_TIMER -> {
                stopTimer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startTimer(durationMins: Int) {
        if (isTimerRunning) return
        isTimerRunning = true

        serviceScope.launch {
            var remainingMillis = durationMins * 60 * 1000L
            while (remainingMillis > 0 && isTimerRunning) {
                timerStateRepository.updateTimerProgress(
                    remaining = remainingMillis,
                    lastUpdated = System.currentTimeMillis(),
                    isRunning = true
                )

                updateNotification("Remaining: ${remainingMillis / 60000}m ${(remainingMillis % 60000) / 1000}s")

                delay(1000)
                remainingMillis -= 1000
            }

            if (remainingMillis <= 0 && isTimerRunning) {
                timerStateRepository.updateTimerProgress(0, System.currentTimeMillis(), false)
                isTimerRunning = false
                updateNotification("Timer Finished!")
            }
        }
    }

    private fun stopTimer() {
        isTimerRunning = false
        serviceScope.launch {
            timerStateRepository.updateTimerProgress(0, System.currentTimeMillis(), false)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the active focus or watch allowance timer"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus Shield Timer Active")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
