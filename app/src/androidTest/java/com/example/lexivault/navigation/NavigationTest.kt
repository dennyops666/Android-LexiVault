package com.example.lexivault.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.lexivault.ui.navigation.AppNavigation
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavGraph() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppNavigation(navController = navController)
        }
    }

    @Test
    fun testStartDestinationIsHome() {
        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("home")
    }

    @Test
    fun testNavigateToWordLibrary() {
        // When
        composeTestRule.onNodeWithContentDescription("词库")
            .performClick()

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("word_library")
    }

    @Test
    fun testNavigateToMemory() {
        // When
        composeTestRule.onNodeWithContentDescription("记忆")
            .performClick()

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("memory")
    }

    @Test
    fun testNavigateToTest() {
        // When
        composeTestRule.onNodeWithContentDescription("测试")
            .performClick()

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("test")
    }

    @Test
    fun testNavigateToWordBook() {
        // When
        composeTestRule.onNodeWithContentDescription("生词本")
            .performClick()

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("word_book")
    }

    @Test
    fun testNavigateToSettings() {
        // When
        composeTestRule.onNodeWithContentDescription("设置")
            .performClick()

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("settings")
    }

    @Test
    fun testBackNavigation() {
        // Given
        composeTestRule.onNodeWithContentDescription("词库")
            .performClick()

        // When
        navController.navigateUp()

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("home")
    }

    @Test
    fun testDeepLinking() {
        // When
        navController.navigate("word_library/1")

        // Then
        assertThat(navController.currentBackStackEntry?.destination?.route)
            .isEqualTo("word_library/{wordId}")
        assertThat(navController.currentBackStackEntry?.arguments?.getString("wordId"))
            .isEqualTo("1")
    }
}
