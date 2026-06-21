package com.focusshield.app.domain.repository

import com.focusshield.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface AppRuleRepository {
    fun getAllRules(): Flow<List<AppRuleEntity>>
    fun getMonitoredApps(): Flow<List<AppRuleEntity>>
    suspend fun getRuleForPackage(packageName: String): AppRuleEntity?
    fun observeRuleForPackage(packageName: String): Flow<AppRuleEntity?>
    suspend fun insertOrUpdate(rule: AppRuleEntity)
    suspend fun insertOrUpdateAll(rules: List<AppRuleEntity>)
    suspend fun setMonitored(packageName: String, isMonitored: Boolean)
    suspend fun delete(rule: AppRuleEntity)
}

interface BlockedEventRepository {
    fun getAllEvents(): Flow<List<BlockedEventEntity>>
    fun getEventsToday(startOfDay: Long): Flow<List<BlockedEventEntity>>
    fun getEventsBetween(startMillis: Long, endMillis: Long): Flow<List<BlockedEventEntity>>
    fun getBlockedCountToday(startOfDay: Long): Flow<Int>
    fun getTimeSavedToday(startOfDay: Long): Flow<Int>
    fun getBlockedCountBetween(startMillis: Long, endMillis: Long): Flow<Int>
    fun getTimeSavedBetween(startMillis: Long, endMillis: Long): Flow<Int>
    suspend fun insert(event: BlockedEventEntity)
    suspend fun deleteOlderThan(beforeMillis: Long)
}

interface StudySessionRepository {
    fun getAllSessions(): Flow<List<StudySessionEntity>>
    fun getActiveSession(): Flow<StudySessionEntity?>
    suspend fun getActiveSessionSync(): StudySessionEntity?
    fun getSessionsBetween(startMillis: Long, endMillis: Long): Flow<List<StudySessionEntity>>
    suspend fun startSession(mode: String, startTime: Long): Long
    suspend fun endSession(id: Long, endTime: Long, completed: Boolean)
}

interface PauseRepository {
    fun getAllPauses(): Flow<List<PauseEventEntity>>
    fun getPausesToday(startOfDay: Long): Flow<List<PauseEventEntity>>
    fun getPauseCountToday(startOfDay: Long): Flow<Int>
    suspend fun insert(event: PauseEventEntity)
}

interface StreakRepository {
    fun getAllStreaks(): Flow<List<StreakEntity>>
    suspend fun getStreakForDate(date: String): StreakEntity?
    fun observeStreakForDate(date: String): Flow<StreakEntity?>
    fun getRecentStreaks(days: Int): Flow<List<StreakEntity>>
    fun getConsecutiveGoalDays(sinceDate: String): Flow<Int>
    suspend fun insertOrUpdate(streak: StreakEntity)
}

interface AppLockRepository {
    fun getAllLocks(): Flow<List<AppLockEntity>>
    fun getLockedApps(): Flow<List<AppLockEntity>>
    suspend fun getLockForPackage(packageName: String): AppLockEntity?
    fun observeLockForPackage(packageName: String): Flow<AppLockEntity?>
    suspend fun insertOrUpdate(lock: AppLockEntity)
    suspend fun delete(lock: AppLockEntity)
}

interface TimerStateRepository {
    suspend fun getTimerState(): TimerStateEntity?
    fun observeTimerState(): Flow<TimerStateEntity?>
    suspend fun insertOrUpdate(state: TimerStateEntity)
    suspend fun clearTimerState()
    suspend fun updateTimerProgress(remaining: Long, lastUpdated: Long, isRunning: Boolean)
}

interface UserPreferencesRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val blockingStyle: Flow<String> // SOFT, MEDIUM, HARD
    val playfulInterruptionsEnabled: Flow<Boolean>
    val strictModeEnabled: Flow<Boolean>
    val selectedGoal: Flow<String> // STUDY, EXAM_PREP, WORK, REDUCE_ADDICTION, CUSTOM
    val pinHash: Flow<String?>

    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setBlockingStyle(style: String)
    suspend fun setPlayfulInterruptionsEnabled(enabled: Boolean)
    suspend fun setStrictModeEnabled(enabled: Boolean)
    suspend fun setSelectedGoal(goal: String)
    suspend fun setPinHash(hash: String?)
}
