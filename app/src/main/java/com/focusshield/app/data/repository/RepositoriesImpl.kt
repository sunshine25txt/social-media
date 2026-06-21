package com.focusshield.app.data.repository

import com.focusshield.app.data.local.dao.*
import com.focusshield.app.data.local.entity.*
import com.focusshield.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRuleRepositoryImpl @Inject constructor(
    private val dao: AppRuleDao
) : AppRuleRepository {
    override fun getAllRules(): Flow<List<AppRuleEntity>> = dao.getAllRules()
    override fun getMonitoredApps(): Flow<List<AppRuleEntity>> = dao.getMonitoredApps()
    override suspend fun getRuleForPackage(packageName: String): AppRuleEntity? = dao.getRuleForPackage(packageName)
    override fun observeRuleForPackage(packageName: String): Flow<AppRuleEntity?> = dao.observeRuleForPackage(packageName)
    override suspend fun insertOrUpdate(rule: AppRuleEntity) = dao.insertOrUpdate(rule)
    override suspend fun insertOrUpdateAll(rules: List<AppRuleEntity>) = dao.insertOrUpdateAll(rules)
    override suspend fun setMonitored(packageName: String, isMonitored: Boolean) = dao.setMonitored(packageName, isMonitored)
    override suspend fun delete(rule: AppRuleEntity) = dao.delete(rule)
}

@Singleton
class BlockedEventRepositoryImpl @Inject constructor(
    private val dao: BlockedEventDao
) : BlockedEventRepository {
    override fun getAllEvents(): Flow<List<BlockedEventEntity>> = dao.getAllEvents()
    override fun getEventsToday(startOfDay: Long): Flow<List<BlockedEventEntity>> = dao.getEventsToday(startOfDay)
    override fun getEventsBetween(startMillis: Long, endMillis: Long): Flow<List<BlockedEventEntity>> = dao.getEventsBetween(startMillis, endMillis)
    override fun getBlockedCountToday(startOfDay: Long): Flow<Int> = dao.getBlockedCountToday(startOfDay)
    override fun getTimeSavedToday(startOfDay: Long): Flow<Int> = dao.getTimeSavedToday(startOfDay)
    override fun getBlockedCountBetween(startMillis: Long, endMillis: Long): Flow<Int> = dao.getBlockedCountBetween(startMillis, endMillis)
    override fun getTimeSavedBetween(startMillis: Long, endMillis: Long): Flow<Int> = dao.getTimeSavedBetween(startMillis, endMillis)
    override suspend fun insert(event: BlockedEventEntity) = dao.insert(event)
    override suspend fun deleteOlderThan(beforeMillis: Long) = dao.deleteOlderThan(beforeMillis)
}

@Singleton
class StudySessionRepositoryImpl @Inject constructor(
    private val dao: StudySessionDao
) : StudySessionRepository {
    override fun getAllSessions(): Flow<List<StudySessionEntity>> = dao.getAllSessions()
    override fun getActiveSession(): Flow<StudySessionEntity?> = dao.getActiveSession()
    override suspend fun getActiveSessionSync(): StudySessionEntity? = dao.getActiveSessionSync()
    override fun getSessionsBetween(startMillis: Long, endMillis: Long): Flow<List<StudySessionEntity>> = dao.getSessionsBetween(startMillis, endMillis)
    override suspend fun startSession(mode: String, startTime: Long): Long {
        val newSession = StudySessionEntity(
            startTime = startTime,
            mode = mode,
            completed = false
        )
        return dao.insert(newSession)
    }
    override suspend fun endSession(id: Long, endTime: Long, completed: Boolean) {
        dao.endSession(id, endTime, completed)
    }
}

@Singleton
class PauseRepositoryImpl @Inject constructor(
    private val dao: PauseEventDao
) : PauseRepository {
    override fun getAllPauses(): Flow<List<PauseEventEntity>> = dao.getAllPauses()
    override fun getPausesToday(startOfDay: Long): Flow<List<PauseEventEntity>> = dao.getPausesToday(startOfDay)
    override fun getPauseCountToday(startOfDay: Long): Flow<Int> = dao.getPauseCountToday(startOfDay)
    override suspend fun insert(event: PauseEventEntity) = dao.insert(event)
}

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val dao: StreakDao
) : StreakRepository {
    override fun getAllStreaks(): Flow<List<StreakEntity>> = dao.getAllStreaks()
    override suspend fun getStreakForDate(date: String): StreakEntity? = dao.getStreakForDate(date)
    override fun observeStreakForDate(date: String): Flow<StreakEntity?> = dao.observeStreakForDate(date)
    override fun getRecentStreaks(days: Int): Flow<List<StreakEntity>> = dao.getRecentStreaks(days)
    override fun getConsecutiveGoalDays(sinceDate: String): Flow<Int> = dao.getConsecutiveGoalDays(sinceDate)
    override suspend fun insertOrUpdate(streak: StreakEntity) = dao.insertOrUpdate(streak)
}

@Singleton
class AppLockRepositoryImpl @Inject constructor(
    private val dao: AppLockDao
) : AppLockRepository {
    override fun getAllLocks(): Flow<List<AppLockEntity>> = dao.getAllLocks()
    override fun getLockedApps(): Flow<List<AppLockEntity>> = dao.getLockedApps()
    override suspend fun getLockForPackage(packageName: String): AppLockEntity? = dao.getLockForPackage(packageName)
    override fun observeLockForPackage(packageName: String): Flow<AppLockEntity?> = dao.observeLockForPackage(packageName)
    override suspend fun insertOrUpdate(lock: AppLockEntity) = dao.insertOrUpdate(lock)
    override suspend fun delete(lock: AppLockEntity) = dao.delete(lock)
}

@Singleton
class TimerStateRepositoryImpl @Inject constructor(
    private val dao: TimerStateDao
) : TimerStateRepository {
    override suspend fun getTimerState(): TimerStateEntity? = dao.getTimerState()
    override fun observeTimerState(): Flow<TimerStateEntity?> = dao.observeTimerState()
    override suspend fun insertOrUpdate(state: TimerStateEntity) = dao.insertOrUpdate(state)
    override suspend fun clearTimerState() = dao.clearTimerState()
    override suspend fun updateTimerProgress(remaining: Long, lastUpdated: Long, isRunning: Boolean) {
        dao.updateTimerProgress(remaining, lastUpdated, isRunning)
    }
}
