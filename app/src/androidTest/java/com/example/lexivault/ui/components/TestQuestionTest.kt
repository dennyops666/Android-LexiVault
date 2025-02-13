package com.example.lexivault.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lexivault.ui.screens.test.TestQuestion
import org.junit.Rule
import org.junit.Test

class TestQuestionTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testQuestionDisplaysCorrectly() {
        // Given
        val word = "test"
        val options = listOf("测试", "例子", "学习", "工作")

        // When
        composeTestRule.setContent {
            TestQuestion(
                word = word,
                options = options,
                onOptionSelected = {},
                isAnswered = false,
                selectedOption = null,
                correctAnswer = "测试"
            )
        }

        // Then
        composeTestRule.onNodeWithText(word).assertIsDisplayed()
        options.forEach { option ->
            composeTestRule.onNodeWithText(option).assertIsDisplayed()
        }
    }

    @Test
    fun testOptionSelection() {
        // Given
        val word = "test"
        val options = listOf("测试", "例子", "学习", "工作")
        var selectedOption: String? = null

        // When
        composeTestRule.setContent {
            TestQuestion(
                word = word,
                options = options,
                onOptionSelected = { selectedOption = it },
                isAnswered = false,
                selectedOption = selectedOption,
                correctAnswer = "测试"
            )
        }

        // Then
        composeTestRule.onNodeWithText("测试").performClick()
        assert(selectedOption == "测试")
    }

    @Test
    fun testCorrectAnswerHighlighting() {
        // Given
        val word = "test"
        val options = listOf("测试", "例子", "学习", "工作")

        // When
        composeTestRule.setContent {
            TestQuestion(
                word = word,
                options = options,
                onOptionSelected = {},
                isAnswered = true,
                selectedOption = "测试",
                correctAnswer = "测试"
            )
        }

        // Then
        composeTestRule.onNodeWithText("测试")
            .assertHasNoClickAction()
            .assertIsDisplayed()
    }

    @Test
    fun testIncorrectAnswerHighlighting() {
        // Given
        val word = "test"
        val options = listOf("测试", "例子", "学习", "工作")

        // When
        composeTestRule.setContent {
            TestQuestion(
                word = word,
                options = options,
                onOptionSelected = {},
                isAnswered = true,
                selectedOption = "例子",
                correctAnswer = "测试"
            )
        }

        // Then
        composeTestRule.onNodeWithText("例子")
            .assertHasNoClickAction()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("测试")
            .assertHasNoClickAction()
            .assertIsDisplayed()
    }
}
