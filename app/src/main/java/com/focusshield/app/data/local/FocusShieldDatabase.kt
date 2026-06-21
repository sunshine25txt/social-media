package com.focusshield.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.focusshield.app.data.local.dao.*
import com.focusshield.app.data.local.entity.*

@Database(
    entities = [
        BlockedEventEntity::class,
        AppRuleEntity::class,
        WebsiteRuleEntity::class,
        StudySessionEntity::class,
        PauseEventEntity::class,
        StreakEntity::class,
        AchievementEntity::class,
        AppLockEntity::class,
        TimerStateEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FocusShieldDatabase : RoomDatabase() {
    abstract fun blockedEventDao(): BlockedEventDao
    abstract fun appRuleDao(): AppRuleDao
    abstract fun websiteRuleDao(): WebsiteRuleDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun pauseEventDao(): PauseEventDao
    abstract fun streakDao(): StreakDao
    abstract fun achievementDao(): AchievementDao
    abstract fun appLockDao(): AppLockDao
    abstract fun timerStateDao(): TimerStateDao
}
