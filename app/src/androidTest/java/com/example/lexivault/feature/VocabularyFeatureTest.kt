package com.example.lexivault.feature

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import com.example.lexivault.data.repository.WordRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class VocabularyFeatureTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var wordRepository: WordRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testImportBuiltInVocabulary() {
        // 进入词库页面
        composeTestRule.onNodeWithText("词库").performClick()
        
        // 导入内置词库
        composeTestRule.onNodeWithText("导入词库").performClick()
        composeTestRule.onNodeWithText("CET4").performClick()
        composeTestRule.onNodeWithText("确认导入").performClick()
        
        // 验证导入结果
        composeTestRule.onNodeWithTag("vocabulary_count")
            .assertTextContains("4000") // CET4词库约4000词
    }

    @Test
    fun testVocabularyClassification() {
        // 进入分类管理
        composeTestRule.onNodeWithText("分类管理").performClick()
        
        // 添加新分类
        composeTestRule.onNodeWithText("添加分类").performClick()
        composeTestRule.onNodeWithTag("category_name").performTextInput("商务英语")
        composeTestRule.onNodeWithText("确认").performClick()
        
        // 验证分类添加成功
        composeTestRule.onNodeWithText("商务英语").assertExists()
        
        // 编辑分类
        composeTestRule.onNodeWithText("商务英语").performLongClick()
        composeTestRule.onNodeWithText("编辑").performClick()
        composeTestRule.onNodeWithTag("category_name").performTextReplacement("职场英语")
        composeTestRule.onNodeWithText("确认").performClick()
        
        // 验证分类编辑成功
        composeTestRule.onNodeWithText("职场英语").assertExists()
    }

    @Test
    fun testLearningProgress() {
        // 进入学习统计
        composeTestRule.onNodeWithText("统计").performClick()
        
        // 验证学习进度显示
        composeTestRule.onNodeWithTag("total_learned").assertExists()
        composeTestRule.onNodeWithTag("daily_learned").assertExists()
        composeTestRule.onNodeWithTag("learning_streak").assertExists()
        
        // 验证进度图表
        composeTestRule.onNodeWithTag("progress_chart").assertExists()
        composeTestRule.onNodeWithTag("daily_chart").assertExists()
    }

    @Test
    fun testDailyLearningRecord() {
        // 开始学习
        composeTestRule.onNodeWithText("开始学习").performClick()
        
        // 学习几个单词
        repeat(5) {
            composeTestRule.onNodeWithTag("word_card").performTouchInput { swipeLeft() }
        }
        
        // 验证今日学习记录
        composeTestRule.onNodeWithText("统计").performClick()
        composeTestRule.onNodeWithTag("today_learned")
            .assertTextContains("5")
    }
}
