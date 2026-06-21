package com.focusshield.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_events")
data class BlockedEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appPackage: String,
    val surfaceType: String, // REEL, SHORT, STORY, EXPLORE, INFINITE_FEED
    val timestampMillis: Long,
    val estimatedSecondsSaved: Int
)

@Entity(tableName = "app_rules")
data class AppRuleEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isMonitored: Boolean = false,
    val blockReels: Boolean = true,
    val blockShorts: Boolean = true,
    val blockStories: Boolean = true,
    val blockFeed: Boolean = false,
    val blockExplore: Boolean = true,
    val dailyLimitMinutes: Int = 0 // 0 = no limit
)

@Entity(tableName = "website_rules")
data class WebsiteRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val domainPattern: String,
    val isBlocked: Boolean = true
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val mode: String, // STUDY, EXAM_PREP, WORK, DEEP_FOCUS
    val completed: Boolean = false
)

@Entity(tableName = "pause_events")
data class PauseEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val durationMinutes: Int,
    val reason: String, // EDUCATIONAL, WORK, EMERGENCY, OTHER
    val appPackage: String? = null
)

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey
    val date: String, // yyyy-MM-dd format
    val focusScore: Int, // 0-100
    val metGoal: Boolean,
    val blockedCount: Int = 0,
    val timeSavedSeconds: Int = 0
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val achievementKey: String,
    val id: Long = 0,
    val unlockedAt: Long? = null,
    val title: String,
    val description: String,
    val iconName: String = "star"
)

@Entity(tableName = "app_locks")
data class AppLockEntity(
    @PrimaryKey
    val packageName: String,
    val lockMethod: String, // PIN, PASSWORD, BIOMETRIC
    val isLocked: Boolean = true,
    val pinHash: String? = null,
    val passwordHash: String? = null
)

@Entity(tableName = "timer_state")
data class TimerStateEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton — only one active timer
    val startTimeMillis: Long,
    val durationMillis: Long,
    val remainingMillis: Long,
    val isRunning: Boolean,
    val associatedApp: String? = null,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)
