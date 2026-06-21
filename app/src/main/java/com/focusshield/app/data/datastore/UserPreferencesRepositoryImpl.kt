package com.focusshield.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.focusshield.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focus_shield_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val context: Context
) : UserPreferencesRepository {

    private val dataStore = context.dataStore

    private object Keys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val BLOCKING_STYLE = stringPreferencesKey("blocking_style")
        val PLAYFUL_INTERRUPTIONS_ENABLED = booleanPreferencesKey("playful_interruptions_enabled")
        val STRICT_MODE_ENABLED = booleanPreferencesKey("strict_mode_enabled")
        val SELECTED_GOAL = stringPreferencesKey("selected_goal")
        val PIN_HASH = stringPreferencesKey("pin_hash")
    }

    override val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_ONBOARDING_COMPLETED] ?: false
    }

    override val blockingStyle: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.BLOCKING_STYLE] ?: "MEDIUM"
    }

    override val playfulInterruptionsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.PLAYFUL_INTERRUPTIONS_ENABLED] ?: true
    }

    override val strictModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.STRICT_MODE_ENABLED] ?: false
    }

    override val selectedGoal: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.SELECTED_GOAL] ?: "REDUCE_ADDICTION"
    }

    override val pinHash: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.PIN_HASH]
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun setBlockingStyle(style: String) {
        dataStore.edit { preferences ->
            preferences[Keys.BLOCKING_STYLE] = style
        }
    }

    override suspend fun setPlayfulInterruptionsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.PLAYFUL_INTERRUPTIONS_ENABLED] = enabled
        }
    }

    override suspend fun setStrictModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.STRICT_MODE_ENABLED] = enabled
        }
    }

    override suspend fun setSelectedGoal(goal: String) {
        dataStore.edit { preferences ->
            preferences[Keys.SELECTED_GOAL] = goal
        }
    }

    override suspend fun setPinHash(hash: String?) {
        dataStore.edit { preferences ->
            if (hash != null) {
                preferences[Keys.PIN_HASH] = hash
            } else {
                preferences.remove(Keys.PIN_HASH)
            }
        }
    }
}
