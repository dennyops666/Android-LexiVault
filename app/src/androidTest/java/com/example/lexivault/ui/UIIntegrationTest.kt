package com.example.lexivault.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import com.example.lexivault.data.entity.Vocabulary
import com.example.lexivault.data.entity.Word
import com.example.lexivault.data.repository.VocabularyRepository
import com.example.lexivault.data.repository.WordRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UIIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var wordRepository: WordRepository

    @Inject
    lateinit var vocabularyRepository: VocabularyRepository

    @Before
    fun setup() {
        hiltRule.inject()
        // 准备测试数据
        runBlocking {
            val vocabulary = Vocabulary(
                id = 1,
                name = "CET4",
                description = "大学英语四级词汇"
            )
            vocabularyRepository.insertVocabulary(vocabulary)

            val word = Word(
                id = 1,
                word = "test",
                meaning = "测试",
                vocabularyId = vocabulary.id,
                category = "noun"
            )
            wordRepository.insertWord(word)
        }
    }

    @Test
    fun testNavigationFlow() {
        // 测试底部导航
        composeTestRule.onNodeWithText("词库").performClick()
        composeTestRule.onNodeWithText("CET4").assertExists()

        composeTestRule.onNodeWithText("学习").performClick()
        composeTestRule.onNodeWithText("开始学习").assertExists()

        composeTestRule.onNodeWithText("记忆").performClick()
        composeTestRule.onNodeWithText("AI助手").assertExists()

        composeTestRule.onNodeWithText("设置").performClick()
        composeTestRule.onNodeWithText("提醒设置").assertExists()
    }

    @Test
    fun testVocabularyListAndDetail() {
        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()

        // 检查词库列表
        composeTestRule.onNodeWithText("CET4").assertExists()
        composeTestRule.onNodeWithText("大学英语四级词汇").assertExists()

        // 点击词库进入详情
        composeTestRule.onNodeWithText("CET4").performClick()

        // 检查词库详情
        composeTestRule.onNodeWithText("test").assertExists()
        composeTestRule.onNodeWithText("测试").assertExists()
    }

    @Test
    fun testWordLearning() {
        // 进入学习页面
        composeTestRule.onNodeWithText("学习").performClick()

        // 开始学习
        composeTestRule.onNodeWithText("开始学习").performClick()

        // 检查学习界面
        composeTestRule.onNodeWithText("test").assertExists()
        composeTestRule.onNodeWithText("测试").assertExists()

        // 测试记忆按钮
        composeTestRule.onNodeWithText("认识").assertExists()
        composeTestRule.onNodeWithText("模糊").assertExists()
        composeTestRule.onNodeWithText("不认识").assertExists()
    }

    @Test
    fun testAIAssistant() {
        // 进入记忆页面
        composeTestRule.onNodeWithText("记忆").performClick()

        // 点击AI助手
        composeTestRule.onNodeWithText("AI助手").performClick()

        // 检查AI助手界面
        composeTestRule.onNodeWithText("生成记忆技巧").assertExists()

        // 输入单词
        composeTestRule.onNodeWithTag("word_input")
            .performTextInput("test")

        // 生成记忆技巧
        composeTestRule.onNodeWithText("生成记忆技巧").performClick()

        // 等待结果
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("memory_technique")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testReminderSettings() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 点击提醒设置
        composeTestRule.onNodeWithText("提醒设置").performClick()

        // 检查提醒设置界面
        composeTestRule.onNodeWithText("开启提醒").assertExists()
        composeTestRule.onNodeWithText("提醒时间").assertExists()
        composeTestRule.onNodeWithText("重复").assertExists()

        // 测试开关
        composeTestRule.onNodeWithTag("reminder_switch").performClick()

        // 测试时间选择
        composeTestRule.onNodeWithTag("time_picker").performClick()
        // 这里需要根据实际的时间选择器实现来补充测试步骤
    }

    @Test
    fun testSearchFunction() {
        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()

        // 点击搜索
        composeTestRule.onNodeWithTag("search_button").performClick()

        // 输入搜索关键词
        composeTestRule.onNodeWithTag("search_input")
            .performTextInput("test")

        // 检查搜索结果
        composeTestRule.onNodeWithText("test").assertExists()
        composeTestRule.onNodeWithText("测试").assertExists()
    }

    @Test
    fun testDataBinding() {
        runBlocking {
            // 更新单词进度
            wordRepository.updateLearningProgress(1, 0.8f)
        }

        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()
        composeTestRule.onNodeWithText("CET4").performClick()

        // 检查进度更新是否反映在UI上
        composeTestRule.onNodeWithTag("progress_indicator_1")
            .assertExists()
            .assert(hasProgressBarRangeInfo(0.8f))
    }
}
