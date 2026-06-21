package com.focusshield.app.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusshield.app.domain.repository.BlockedEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyStats(
    val dateLabel: String,
    val timeSavedMins: Int,
    val blockedCount: Int
)

data class StatisticsUiState(
    val weeklyData: List<DailyStats> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val blockedEventRepository: BlockedEventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            val mockData = listOf(
                DailyStats("Mon", 45, 12),
                DailyStats("Tue", 60, 15),
                DailyStats("Wed", 30, 8),
                DailyStats("Thu", 90, 20),
                DailyStats("Fri", 120, 25),
                DailyStats("Sat", 20, 5),
                DailyStats("Sun", 40, 10)
            )
            _uiState.value = StatisticsUiState(weeklyData = mockData, isLoading = false)
        }
    }
}
