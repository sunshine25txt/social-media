package com.focusshield.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusshield.app.domain.repository.BlockedEventRepository
import com.focusshield.app.domain.repository.StreakRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val timeSavedTodaySecs: Int = 0,
    val blockedCountToday: Int = 0,
    val currentStreakDays: Int = 0,
    val focusScore: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val blockedEventRepository: BlockedEventRepository,
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val startOfDayMillis: Long
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

    private val todayDateString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val uiState: StateFlow<DashboardUiState> = combine(
        blockedEventRepository.getTimeSavedToday(startOfDayMillis),
        blockedEventRepository.getBlockedCountToday(startOfDayMillis),
        streakRepository.getConsecutiveGoalDays(todayDateString)
    ) { timeSaved, blockedCount, streak ->
        DashboardUiState(
            timeSavedTodaySecs = timeSaved,
            blockedCountToday = blockedCount,
            currentStreakDays = streak,
            focusScore = calculateFocusScore(timeSaved, blockedCount),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun calculateFocusScore(timeSavedSecs: Int, blockedCount: Int): Int {
        val timeSavedMins = timeSavedSecs / 60
        val baseScore = timeSavedMins * 2 + blockedCount * 5
        return baseScore.coerceAtMost(100)
    }
}
