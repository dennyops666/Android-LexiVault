package com.example.lexivault.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lexivault.ui.common.SearchBar
import org.junit.Rule
import org.junit.Test

class SearchBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchBarDisplaysCorrectly() {
        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                placeholder = "搜索单词"
            )
        }

        // Then
        composeTestRule.onNodeWithText("搜索单词").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("搜索").assertIsDisplayed()
    }

    @Test
    fun testSearchBarTextInput() {
        // Given
        var searchQuery = ""

        // When
        composeTestRule.setContent {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {},
                placeholder = "搜索单词"
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("搜索输入框")
            .performTextInput("test")
        assert(searchQuery == "test")
    }

    @Test
    fun testSearchBarSearchAction() {
        // Given
        var searchTriggered = false

        // When
        composeTestRule.setContent {
            SearchBar(
                query = "test",
                onQueryChange = {},
                onSearch = { searchTriggered = true },
                placeholder = "搜索单词"
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("搜索").performClick()
        assert(searchTriggered)
    }

    @Test
    fun testSearchBarClearButton() {
        // Given
        var searchQuery = "test"

        // When
        composeTestRule.setContent {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {},
                placeholder = "搜索单词"
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("清除搜索").performClick()
        assert(searchQuery.isEmpty())
    }

    @Test
    fun testSearchBarKeyboardActions() {
        // Given
        var searchTriggered = false

        // When
        composeTestRule.setContent {
            SearchBar(
                query = "test",
                onQueryChange = {},
                onSearch = { searchTriggered = true },
                placeholder = "搜索单词"
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("搜索输入框")
            .performImeAction()
        assert(searchTriggered)
    }
}
