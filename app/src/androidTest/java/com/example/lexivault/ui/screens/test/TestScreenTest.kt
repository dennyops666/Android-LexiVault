package com.example.lexivault.ui.screens.test

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class TestScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun multipleChoice_displays_question_and_options() {
        // Given
        val testItem = TestItem(
            word = "test",
            meaning = "测试",
            type = TestType.MULTIPLE_CHOICE,
            options = listOf("测试", "例子", "练习", "学习")
        )

        // When
        composeTestRule.setContent {
            TestScreen()
        }

        // Then
        composeTestRule.onNodeWithText("test").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("option").assertCountEquals(4)
    }

    @Test
    fun spelling_test_allows_input_and_submission() {
        // Given
        val testItem = TestItem(
            word = "test",
            meaning = "测试",
            type = TestType.SPELLING
        )

        // When
        composeTestRule.setContent {
            TestScreen()
        }

        // Then
        composeTestRule.onNodeWithText("测试").assertIsDisplayed()
        
        // Input answer
        composeTestRule.onNode(hasSetTextAction()).performTextInput("test")
        composeTestRule.onNodeWithText("提交").performClick()
        
        // Verify feedback is shown
        composeTestRule.onNodeWithText("回答正确！").assertIsDisplayed()
    }

    @Test
    fun test_completion_shows_results() {
        // When
        composeTestRule.setContent {
            TestScreen()
        }

        // Complete all questions
        repeat(5) {
            composeTestRule.onNodeWithText("跳过").performClick()
        }

        // Then
        composeTestRule.onNodeWithText("测试已完成！").assertIsDisplayed()
        composeTestRule.onNodeWithText(containsString("正确率")).assertIsDisplayed()
    }

    @Test
    fun wrong_answer_shows_correct_answer() {
        // Given
        val testItem = TestItem(
            word = "test",
            meaning = "测试",
            type = TestType.SPELLING
        )

        // When
        composeTestRule.setContent {
            TestScreen()
        }

        // Input wrong answer
        composeTestRule.onNode(hasSetTextAction()).performTextInput("wrong")
        composeTestRule.onNodeWithText("提交").performClick()

        // Then
        composeTestRule.onNodeWithText("回答错误").assertIsDisplayed()
        composeTestRule.onNodeWithText("正确答案：test").assertIsDisplayed()
    }
}
