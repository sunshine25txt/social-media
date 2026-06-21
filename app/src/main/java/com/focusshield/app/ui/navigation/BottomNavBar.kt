package com.focusshield.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class TabItem(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : TabItem(Screen.Dashboard.route, Icons.Default.Dashboard, "Home")
    object Focus : TabItem(Screen.Focus.route, Icons.Default.HourglassTop, "Focus")
    object Statistics : TabItem(Screen.Statistics.route, Icons.Default.BarChart, "Stats")
    object Settings : TabItem(Screen.Settings.route, Icons.Default.Settings, "Settings")
}

@Composable
fun BottomNavBar(navController: NavController) {
    val tabs = listOf(
        TabItem.Dashboard,
        TabItem.Focus,
        TabItem.Statistics,
        TabItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Verify if current route is one of the bottom bar tabs, otherwise hide bottom bar
    val showBottomBar = tabs.any { it.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            tabs.forEach { tab ->
                val isSelected = currentRoute == tab.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != tab.route) {
                            navController.navigate(tab.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(Screen.Dashboard.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label
                        )
                    },
                    label = { Text(tab.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
