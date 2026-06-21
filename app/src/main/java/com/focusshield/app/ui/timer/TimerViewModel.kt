package com.focusshield.app.ui.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusshield.app.domain.repository.TimerStateRepository
import com.focusshield.app.service.foreground.FocusForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerStateRepository: TimerStateRepository
) : ViewModel() {

    val isTimerRunning: StateFlow<Boolean> = timerStateRepository.observeTimerState()
        .map { it?.isRunning == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun startTimer(context: Context, durationMins: Int) {
        val intent = Intent(context, FocusForegroundService::class.java).apply {
            action = FocusForegroundService.ACTION_START_TIMER
            putExtra(FocusForegroundService.EXTRA_DURATION_MINS, durationMins)
        }
        context.startForegroundService(intent)
    }

    fun stopTimer(context: Context) {
        val intent = Intent(context, FocusForegroundService::class.java).apply {
            action = FocusForegroundService.ACTION_STOP_TIMER
        }
        context.startService(intent)
    }
}
