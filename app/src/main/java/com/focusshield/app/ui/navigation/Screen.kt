package com.focusshield.app.ui.navigation

sealed class Screen(val route: String) {
    // Onboarding screens
    object Welcome : Screen("welcome")
    object GoalSelection : Screen("goal_selection")
    object AppSelection : Screen("app_selection")
    object BlockingStyle : Screen("blocking_style")
    object AccessibilityDisclosure : Screen("accessibility_disclosure")
    object PermissionSetup : Screen("permission_setup")

    // Main App tabs
    object Dashboard : Screen("dashboard")
    object Focus : Screen("focus")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")

    // Sub-flows
    object WatchTimerSetup : Screen("watch_timer_setup")
    object AppLockSetup : Screen("app_lock_setup")
}
