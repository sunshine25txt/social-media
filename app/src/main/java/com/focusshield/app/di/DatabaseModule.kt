package com.focusshield.app.di

import android.content.Context
import androidx.room.Room
import com.focusshield.app.data.local.FocusShieldDatabase
import com.focusshield.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusShieldDatabase {
        return Room.databaseBuilder(
            context,
            FocusShieldDatabase::class.java,
            "focus_shield_database"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideBlockedEventDao(db: FocusShieldDatabase): BlockedEventDao = db.blockedEventDao()

    @Provides
    fun provideAppRuleDao(db: FocusShieldDatabase): AppRuleDao = db.appRuleDao()

    @Provides
    fun provideWebsiteRuleDao(db: FocusShieldDatabase): WebsiteRuleDao = db.websiteRuleDao()

    @Provides
    fun provideStudySessionDao(db: FocusShieldDatabase): StudySessionDao = db.studySessionDao()

    @Provides
    fun providePauseEventDao(db: FocusShieldDatabase): PauseEventDao = db.pauseEventDao()

    @Provides
    fun provideStreakDao(db: FocusShieldDatabase): StreakDao = db.streakDao()

    @Provides
    fun provideAchievementDao(db: FocusShieldDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideAppLockDao(db: FocusShieldDatabase): AppLockDao = db.appLockDao()

    @Provides
    fun provideTimerStateDao(db: FocusShieldDatabase): TimerStateDao = db.timerStateDao()
}
