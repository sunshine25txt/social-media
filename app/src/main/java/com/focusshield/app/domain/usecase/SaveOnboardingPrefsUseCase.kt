package com.focusshield.app.domain.usecase

import com.focusshield.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveOnboardingPrefsUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend fun execute(goal: String, blockingStyle: String) {
        repository.setSelectedGoal(goal)
        repository.setBlockingStyle(blockingStyle)
        repository.setOnboardingCompleted(true)
    }
}
