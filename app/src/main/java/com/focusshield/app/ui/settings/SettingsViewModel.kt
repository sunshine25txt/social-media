package com.focusshield.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusshield.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val playfulInterruptions: Boolean = false,
    val strictMode: Boolean = false,
    val blockingStyle: String = "MEDIUM"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPrefsRepository.playfulInterruptionsEnabled,
        userPrefsRepository.strictModeEnabled,
        userPrefsRepository.blockingStyle
    ) { playful, strict, style ->
        SettingsUiState(
            playfulInterruptions = playful,
            strictMode = strict,
            blockingStyle = style
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun togglePlayfulInterruptions(enabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.setPlayfulInterruptionsEnabled(enabled)
        }
    }

    fun toggleStrictMode(enabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.setStrictModeEnabled(enabled)
        }
    }

    fun setBlockingStyle(style: String) {
        viewModelScope.launch {
            userPrefsRepository.setBlockingStyle(style)
        }
    }
}
