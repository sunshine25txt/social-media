package com.focusshield.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.focusshield.app.ui.applock.AppLockSetupScreen
import com.focusshield.app.ui.applock.AppLockViewModel
import com.focusshield.app.ui.focus.StudyModeScreen
import com.focusshield.app.ui.focus.StudyModeViewModel
import com.focusshield.app.ui.home.DashboardScreen
import com.focusshield.app.ui.home.DashboardViewModel
import com.focusshield.app.ui.onboarding.OnboardingViewModel
import com.focusshield.app.ui.onboarding.OnboardingWizardScreen
import com.focusshield.app.ui.settings.SettingsScreen
import com.focusshield.app.ui.settings.SettingsViewModel
import com.focusshield.app.ui.statistics.StatisticsScreen
import com.focusshield.app.ui.statistics.StatisticsViewModel
import com.focusshield.app.ui.timer.WatchTimerSetupScreen
import com.focusshield.app.ui.timer.TimerViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("onboarding") {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnboardingWizardScreen(
                viewModel = viewModel,
                onOnboardingComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToTimerSetup = {
                    navController.navigate(Screen.WatchTimerSetup.route)
                }
            )
        }

        composable(Screen.Focus.route) {
            val viewModel: StudyModeViewModel = hiltViewModel()
            StudyModeScreen(viewModel = viewModel)
        }

        composable(Screen.Statistics.route) {
            val viewModel: StatisticsViewModel = hiltViewModel()
            StatisticsScreen(viewModel = viewModel)
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToAppLockSetup = {
                    navController.navigate(Screen.AppLockSetup.route)
                }
            )
        }

        composable(Screen.WatchTimerSetup.route) {
            val viewModel: TimerViewModel = hiltViewModel()
            WatchTimerSetupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AppLockSetup.route) {
            val viewModel: AppLockViewModel = hiltViewModel()
            AppLockSetupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
