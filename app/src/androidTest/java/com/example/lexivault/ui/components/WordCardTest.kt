package com.example.lexivault.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lexivault.data.database.entity.WordEntity
import org.junit.Rule
import org.junit.Test

class WordCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun wordCard_displays_word_and_phonetic() {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            phonetic = "/test/",
            category = "noun",
            type = "CET4"
        )

        // When
        composeTestRule.setContent {
            WordCard(
                word = word,
                onBookmark = {},
                onNextWord = {},
                onPreviousWord = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("test").assertIsDisplayed()
        composeTestRule.onNodeWithText("/test/").assertIsDisplayed()
    }

    @Test
    fun wordCard_flips_when_clicked() {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4"
        )

        // When
        composeTestRule.setContent {
            WordCard(
                word = word,
                onBookmark = {},
                onNextWord = {},
                onPreviousWord = {}
            )
        }

        // Click to flip
        composeTestRule.onNodeWithText("查看释义").performClick()

        // Then
        composeTestRule.onNodeWithText("测试").assertIsDisplayed()
        composeTestRule.onNodeWithText("查看单词").assertIsDisplayed()
    }

    @Test
    fun wordCard_bookmark_button_works() {
        // Given
        var isBookmarked = false
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4",
            isBookmarked = isBookmarked
        )

        // When
        composeTestRule.setContent {
            WordCard(
                word = word,
                onBookmark = { isBookmarked = it },
                onNextWord = {},
                onPreviousWord = {}
            )
        }

        // Click bookmark button
        composeTestRule.onNodeWithContentDescription("Add to wordbook").performClick()

        // Then
        assert(isBookmarked)
    }
}
