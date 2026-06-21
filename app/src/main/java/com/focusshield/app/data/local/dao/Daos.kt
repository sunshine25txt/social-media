package com.focusshield.app.data.local.dao

import androidx.room.*
import com.focusshield.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedEventDao {
    @Insert
    suspend fun insert(event: BlockedEventEntity)

    @Query("SELECT * FROM blocked_events ORDER BY timestampMillis DESC")
    fun getAllEvents(): Flow<List<BlockedEventEntity>>

    @Query("SELECT * FROM blocked_events WHERE timestampMillis >= :startOfDay ORDER BY timestampMillis DESC")
    fun getEventsToday(startOfDay: Long): Flow<List<BlockedEventEntity>>

    @Query("SELECT * FROM blocked_events WHERE timestampMillis >= :startMillis AND timestampMillis <= :endMillis ORDER BY timestampMillis DESC")
    fun getEventsBetween(startMillis: Long, endMillis: Long): Flow<List<BlockedEventEntity>>

    @Query("SELECT COUNT(*) FROM blocked_events WHERE timestampMillis >= :startOfDay")
    fun getBlockedCountToday(startOfDay: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(estimatedSecondsSaved), 0) FROM blocked_events WHERE timestampMillis >= :startOfDay")
    fun getTimeSavedToday(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_events WHERE timestampMillis >= :startMillis AND timestampMillis <= :endMillis")
    fun getBlockedCountBetween(startMillis: Long, endMillis: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(estimatedSecondsSaved), 0) FROM blocked_events WHERE timestampMillis >= :startMillis AND timestampMillis <= :endMillis")
    fun getTimeSavedBetween(startMillis: Long, endMillis: Long): Flow<Int>

    @Query("DELETE FROM blocked_events WHERE timestampMillis < :beforeMillis")
    suspend fun deleteOlderThan(beforeMillis: Long)
}

@Dao
interface AppRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(rule: AppRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(rules: List<AppRuleEntity>)

    @Query("SELECT * FROM app_rules ORDER BY appName ASC")
    fun getAllRules(): Flow<List<AppRuleEntity>>

    @Query("SELECT * FROM app_rules WHERE isMonitored = 1")
    fun getMonitoredApps(): Flow<List<AppRuleEntity>>

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    suspend fun getRuleForPackage(packageName: String): AppRuleEntity?

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    fun observeRuleForPackage(packageName: String): Flow<AppRuleEntity?>

    @Query("UPDATE app_rules SET isMonitored = :isMonitored WHERE packageName = :packageName")
    suspend fun setMonitored(packageName: String, isMonitored: Boolean)

    @Delete
    suspend fun delete(rule: AppRuleEntity)
}

@Dao
interface WebsiteRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(rule: WebsiteRuleEntity)

    @Query("SELECT * FROM website_rules ORDER BY domainPattern ASC")
    fun getAllRules(): Flow<List<WebsiteRuleEntity>>

    @Query("SELECT * FROM website_rules WHERE isBlocked = 1")
    fun getBlockedWebsites(): Flow<List<WebsiteRuleEntity>>

    @Delete
    suspend fun delete(rule: WebsiteRuleEntity)
}

@Dao
interface StudySessionDao {
    @Insert
    suspend fun insert(session: StudySessionEntity): Long

    @Update
    suspend fun update(session: StudySessionEntity)

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE endTime IS NULL LIMIT 1")
    fun getActiveSession(): Flow<StudySessionEntity?>

    @Query("SELECT * FROM study_sessions WHERE endTime IS NULL LIMIT 1")
    suspend fun getActiveSessionSync(): StudySessionEntity?

    @Query("SELECT * FROM study_sessions WHERE startTime >= :startMillis AND startTime <= :endMillis")
    fun getSessionsBetween(startMillis: Long, endMillis: Long): Flow<List<StudySessionEntity>>

    @Query("UPDATE study_sessions SET endTime = :endTime, completed = :completed WHERE id = :id")
    suspend fun endSession(id: Long, endTime: Long, completed: Boolean)
}

@Dao
interface PauseEventDao {
    @Insert
    suspend fun insert(event: PauseEventEntity)

    @Query("SELECT * FROM pause_events ORDER BY timestamp DESC")
    fun getAllPauses(): Flow<List<PauseEventEntity>>

    @Query("SELECT * FROM pause_events WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getPausesToday(startOfDay: Long): Flow<List<PauseEventEntity>>

    @Query("SELECT COUNT(*) FROM pause_events WHERE timestamp >= :startOfDay")
    fun getPauseCountToday(startOfDay: Long): Flow<Int>
}

@Dao
interface StreakDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: StreakEntity)

    @Query("SELECT * FROM streaks ORDER BY date DESC")
    fun getAllStreaks(): Flow<List<StreakEntity>>

    @Query("SELECT * FROM streaks WHERE date = :date")
    suspend fun getStreakForDate(date: String): StreakEntity?

    @Query("SELECT * FROM streaks WHERE date = :date")
    fun observeStreakForDate(date: String): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks ORDER BY date DESC LIMIT :days")
    fun getRecentStreaks(days: Int): Flow<List<StreakEntity>>

    @Query("SELECT COUNT(*) FROM streaks WHERE metGoal = 1 AND date >= :sinceDate ORDER BY date DESC")
    fun getConsecutiveGoalDays(sinceDate: String): Flow<Int>
}

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievements ORDER BY unlockedAt DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE achievementKey = :key")
    suspend fun getAchievement(key: String): AchievementEntity?

    @Query("UPDATE achievements SET unlockedAt = :unlockedAt WHERE achievementKey = :key")
    suspend fun unlockAchievement(key: String, unlockedAt: Long)
}

@Dao
interface AppLockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(lock: AppLockEntity)

    @Query("SELECT * FROM app_locks ORDER BY packageName ASC")
    fun getAllLocks(): Flow<List<AppLockEntity>>

    @Query("SELECT * FROM app_locks WHERE isLocked = 1")
    fun getLockedApps(): Flow<List<AppLockEntity>>

    @Query("SELECT * FROM app_locks WHERE packageName = :packageName")
    suspend fun getLockForPackage(packageName: String): AppLockEntity?

    @Query("SELECT * FROM app_locks WHERE packageName = :packageName")
    fun observeLockForPackage(packageName: String): Flow<AppLockEntity?>

    @Delete
    suspend fun delete(lock: AppLockEntity)
}

@Dao
interface TimerStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(state: TimerStateEntity)

    @Query("SELECT * FROM timer_state WHERE id = 1")
    suspend fun getTimerState(): TimerStateEntity?

    @Query("SELECT * FROM timer_state WHERE id = 1")
    fun observeTimerState(): Flow<TimerStateEntity?>

    @Query("DELETE FROM timer_state WHERE id = 1")
    suspend fun clearTimerState()

    @Query("UPDATE timer_state SET remainingMillis = :remaining, lastUpdatedMillis = :lastUpdated, isRunning = :isRunning WHERE id = 1")
    suspend fun updateTimerProgress(remaining: Long, lastUpdated: Long, isRunning: Boolean)
}
