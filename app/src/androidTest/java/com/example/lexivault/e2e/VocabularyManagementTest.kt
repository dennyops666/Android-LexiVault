package com.example.lexivault.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class VocabularyManagementTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testCreateAndManageVocabulary() {
        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()

        // 创建新词库
        composeTestRule.onNodeWithTag("add_vocabulary").performClick()
        composeTestRule.onNodeWithTag("vocabulary_name")
            .performTextInput("IELTS")
        composeTestRule.onNodeWithTag("vocabulary_description")
            .performTextInput("雅思词汇")
        composeTestRule.onNodeWithText("确认").performClick()

        // 验证词库创建成功
        composeTestRule.onNodeWithText("IELTS").assertExists()
        composeTestRule.onNodeWithText("雅思词汇").assertExists()

        // 编辑词库
        composeTestRule.onNodeWithText("IELTS").performLongClick()
        composeTestRule.onNodeWithText("编辑").performClick()
        composeTestRule.onNodeWithTag("vocabulary_description")
            .performTextReplacement("雅思核心词汇")
        composeTestRule.onNodeWithText("确认").performClick()

        // 验证编辑成功
        composeTestRule.onNodeWithText("雅思核心词汇").assertExists()

        // 添加单词
        composeTestRule.onNodeWithText("IELTS").performClick()
        composeTestRule.onNodeWithTag("add_word").performClick()
        composeTestRule.onNodeWithTag("word_input")
            .performTextInput("academic")
        composeTestRule.onNodeWithTag("meaning_input")
            .performTextInput("学术的")
        composeTestRule.onNodeWithTag("category_input")
            .performTextInput("adj")
        composeTestRule.onNodeWithText("确认").performClick()

        // 验证单词添加成功
        composeTestRule.onNodeWithText("academic").assertExists()
        composeTestRule.onNodeWithText("学术的").assertExists()

        // 删除单词
        composeTestRule.onNodeWithText("academic").performLongClick()
        composeTestRule.onNodeWithText("删除").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 验证单词删除成功
        composeTestRule.onNodeWithText("academic").assertDoesNotExist()

        // 删除词库
        composeTestRule.onNodeWithContentDescription("返回").performClick()
        composeTestRule.onNodeWithText("IELTS").performLongClick()
        composeTestRule.onNodeWithText("删除").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 验证词库删除成功
        composeTestRule.onNodeWithText("IELTS").assertDoesNotExist()
    }

    @Test
    fun testVocabularyImportExport() {
        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()

        // 导入词库
        composeTestRule.onNodeWithTag("more_options").performClick()
        composeTestRule.onNodeWithText("导入词库").performClick()
        // 模拟文件选择
        // 注意：实际的文件选择可能需要使用UIAutomator或其他工具

        // 验证导入成功
        composeTestRule.onNodeWithText("导入成功").assertExists()
        composeTestRule.onNodeWithText("CET6").assertExists()

        // 导出词库
        composeTestRule.onNodeWithText("CET6").performLongClick()
        composeTestRule.onNodeWithText("导出").performClick()
        // 模拟文件保存
        // 注意：实际的文件保存可能需要使用UIAutomator或其他工具

        // 验证导出成功
        composeTestRule.onNodeWithText("导出成功").assertExists()
    }

    @Test
    fun testVocabularySearch() {
        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()

        // 创建测试词库
        composeTestRule.onNodeWithTag("add_vocabulary").performClick()
        composeTestRule.onNodeWithTag("vocabulary_name")
            .performTextInput("Test Vocabulary")
        composeTestRule.onNodeWithText("确认").performClick()

        // 添加测试单词
        composeTestRule.onNodeWithText("Test Vocabulary").performClick()
        composeTestRule.onNodeWithTag("add_word").performClick()
        composeTestRule.onNodeWithTag("word_input")
            .performTextInput("test")
        composeTestRule.onNodeWithTag("meaning_input")
            .performTextInput("测试")
        composeTestRule.onNodeWithText("确认").performClick()

        // 搜索词库
        composeTestRule.onNodeWithContentDescription("返回").performClick()
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_input")
            .performTextInput("Test")

        // 验证搜索结果
        composeTestRule.onNodeWithText("Test Vocabulary").assertExists()

        // 进入词库搜索单词
        composeTestRule.onNodeWithText("Test Vocabulary").performClick()
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_input")
            .performTextInput("test")

        // 验证单词搜索结果
        composeTestRule.onNodeWithText("test").assertExists()
        composeTestRule.onNodeWithText("测试").assertExists()
    }
}
