package com.focusshield.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.focusshield.app.domain.repository.UserPreferencesRepository
import com.focusshield.app.ui.navigation.BottomNavBar
import com.focusshield.app.ui.navigation.NavGraph
import com.focusshield.app.ui.theme.FocusShieldTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent flicker by checking preferences before layout inflation
        val onboardingCompleted = runBlocking {
            userPreferencesRepository.isOnboardingCompleted.first()
        }

        setContent {
            FocusShieldTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { paddingValues ->
                    NavGraph(
                        navController = navController,
                        paddingValues = paddingValues,
                        startDestination = if (onboardingCompleted) "dashboard" else "onboarding"
                    )
                }
            }
        }
    }
}
