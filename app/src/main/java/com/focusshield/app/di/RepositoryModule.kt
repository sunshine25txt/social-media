package com.focusshield.app.di

import com.focusshield.app.data.datastore.UserPreferencesRepositoryImpl
import com.focusshield.app.data.repository.*
import com.focusshield.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppRuleRepository(
        impl: AppRuleRepositoryImpl
    ): AppRuleRepository

    @Binds
    @Singleton
    abstract fun bindBlockedEventRepository(
        impl: BlockedEventRepositoryImpl
    ): BlockedEventRepository

    @Binds
    @Singleton
    abstract fun bindStudySessionRepository(
        impl: StudySessionRepositoryImpl
    ): StudySessionRepository

    @Binds
    @Singleton
    abstract fun bindPauseRepository(
        impl: PauseRepositoryImpl
    ): PauseRepository

    @Binds
    @Singleton
    abstract fun bindStreakRepository(
        impl: StreakRepositoryImpl
    ): StreakRepository

    @Binds
    @Singleton
    abstract fun bindAppLockRepository(
        impl: AppLockRepositoryImpl
    ): AppLockRepository

    @Binds
    @Singleton
    abstract fun bindTimerStateRepository(
        impl: TimerStateRepositoryImpl
    ): TimerStateRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
