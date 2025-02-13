package com.example.lexivault.feature

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import com.example.lexivault.data.repository.TestRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TestModeFeatureTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var testRepository: TestRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testMultipleChoiceTest() {
        // 进入测试模式
        composeTestRule.onNodeWithText("测试").performClick()
        
        // 选择选择题模式
        composeTestRule.onNodeWithText("选择题").performClick()
        
        // 开始测试
        composeTestRule.onNodeWithText("开始测试").performClick()
        
        // 完成一道选择题
        composeTestRule.onNodeWithTag("choice_options").performClick()
        composeTestRule.onNodeWithText("下一题").performClick()
        
        // 验证答题记录
        composeTestRule.onNodeWithTag("answer_record").assertExists()
    }

    @Test
    fun testSpellingTest() {
        // 进入测试模式
        composeTestRule.onNodeWithText("测试").performClick()
        
        // 选择拼写测试模式
        composeTestRule.onNodeWithText("拼写测试").performClick()
        
        // 开始测试
        composeTestRule.onNodeWithText("开始测试").performClick()
        
        // 输入拼写
        composeTestRule.onNodeWithTag("spelling_input")
            .performTextInput("test")
        composeTestRule.onNodeWithText("确认").performClick()
        
        // 验证拼写结果
        composeTestRule.onNodeWithTag("spelling_result").assertExists()
    }

    @Test
    fun testMistakeBook() {
        // 进入错题本
        composeTestRule.onNodeWithText("错题本").performClick()
        
        // 验证错题列表
        composeTestRule.onNodeWithTag("mistake_list").assertExists()
        
        // 开始错题复习
        composeTestRule.onNodeWithText("开始复习").performClick()
        
        // 完成一道错题复习
        composeTestRule.onNodeWithTag("choice_options").performClick()
        composeTestRule.onNodeWithText("下一题").performClick()
        
        // 验证复习记录
        composeTestRule.onNodeWithTag("review_record").assertExists()
    }

    @Test
    fun testTestResult() {
        // 完成一次测试
        composeTestRule.onNodeWithText("测试").performClick()
        composeTestRule.onNodeWithText("选择题").performClick()
        composeTestRule.onNodeWithText("开始测试").performClick()
        
        repeat(5) {
            composeTestRule.onNodeWithTag("choice_options").performClick()
            composeTestRule.onNodeWithText("下一题").performClick()
        }
        
        // 验证测试结果
        composeTestRule.onNodeWithTag("test_score").assertExists()
        composeTestRule.onNodeWithTag("correct_rate").assertExists()
        composeTestRule.onNodeWithTag("mistake_analysis").assertExists()
    }
}
