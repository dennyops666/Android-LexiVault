package com.example.lexivault.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lexivault.ui.screens.home.HomeScreen
import com.example.lexivault.ui.screens.library.WordLibraryScreen
import com.example.lexivault.ui.screens.memory.MemoryScreen
import com.example.lexivault.ui.screens.mistakes.MistakesScreen
import com.example.lexivault.ui.screens.settings.ReminderSettingsScreen
import com.example.lexivault.ui.screens.test.TestScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                onNavigateToMemory = { navController.navigate(Screen.Memory.route) },
                onNavigateToTest = { navController.navigate(Screen.Test.route) },
                onNavigateToMistakes = { navController.navigate(Screen.Mistakes.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Library.route) {
            WordLibraryScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Memory.route) {
            MemoryScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Test.route) {
            TestScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Mistakes.route) {
            MistakesScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Settings.route) {
            ReminderSettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Memory : Screen("memory")
    object Test : Screen("test")
    object Mistakes : Screen("mistakes")
    object Settings : Screen("settings")
}
