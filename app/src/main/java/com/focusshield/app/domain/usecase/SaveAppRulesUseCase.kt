package com.focusshield.app.domain.usecase

import com.focusshield.app.data.local.entity.AppRuleEntity
import com.focusshield.app.domain.repository.AppRuleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveAppRulesUseCase @Inject constructor(
    private val repository: AppRuleRepository
) {
    suspend fun execute(selectedPackages: List<String>, allPackagesWithNames: Map<String, String>) {
        val rules = allPackagesWithNames.map { (pkg, name) ->
            AppRuleEntity(
                packageName = pkg,
                appName = name,
                isMonitored = selectedPackages.contains(pkg),
                blockReels = true,
                blockShorts = true,
                blockStories = true,
                blockFeed = false,
                blockExplore = true,
                dailyLimitMinutes = 0
            )
        }
        repository.insertOrUpdateAll(rules)
    }
}
