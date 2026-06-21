package com.focusshield.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusshield.app.domain.usecase.CheckPermissionStatusUseCase
import com.focusshield.app.domain.usecase.PermissionStates
import com.focusshield.app.domain.usecase.SaveAppRulesUseCase
import com.focusshield.app.domain.usecase.SaveOnboardingPrefsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val checkPermissionStatusUseCase: CheckPermissionStatusUseCase,
    private val saveOnboardingPrefsUseCase: SaveOnboardingPrefsUseCase,
    private val saveAppRulesUseCase: SaveAppRulesUseCase
) : ViewModel() {

    // Target packages with their display names
    val availableApps = mapOf(
        "com.instagram.android" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.google.android.youtube" to "YouTube",
        "com.kwai.video" to "SnackVideo", // SnackVideo
        "com.facebook.katana" to "Facebook",
        "com.facebook.lite" to "Facebook Lite"
    )

    private val _selectedGoal = MutableStateFlow("REDUCE_ADDICTION")
    val selectedGoal: StateFlow<String> = _selectedGoal.asStateFlow()

    private val _selectedApps = MutableStateFlow<Set<String>>(availableApps.keys)
    val selectedApps: StateFlow<Set<String>> = _selectedApps.asStateFlow()

    private val _blockingStyle = MutableStateFlow("MEDIUM")
    val blockingStyle: StateFlow<String> = _blockingStyle.asStateFlow()

    private val _permissionStates = MutableStateFlow(
        PermissionStates(
            hasAccessibility = false,
            hasUsageStats = false,
            hasOverlay = false,
            hasNotifications = false
        )
    )
    val permissionStates: StateFlow<PermissionStates> = _permissionStates.asStateFlow()

    init {
        checkPermissions()
    }

    fun setGoal(goal: String) {
        _selectedGoal.value = goal
    }

    fun toggleAppSelection(packageName: String) {
        _selectedApps.value = if (_selectedApps.value.contains(packageName)) {
            _selectedApps.value - packageName
        } else {
            _selectedApps.value + packageName
        }
    }

    fun setBlockingStyle(style: String) {
        _blockingStyle.value = style
    }

    fun checkPermissions() {
        _permissionStates.value = checkPermissionStatusUseCase.execute()
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Save preferences
            saveOnboardingPrefsUseCase.execute(
                goal = _selectedGoal.value,
                blockingStyle = _blockingStyle.value
            )
            // Save monitoring rules
            saveAppRulesUseCase.execute(
                selectedPackages = _selectedApps.value.toList(),
                allPackagesWithNames = availableApps
            )
            onSuccess()
        }
    }
}
