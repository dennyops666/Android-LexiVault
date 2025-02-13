package com.example.lexivault.e2e

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
class WordLearningTest {
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
                name = "IELTS",
                description = "雅思词汇"
            )
            vocabularyRepository.insertVocabulary(vocabulary)

            val words = listOf(
                Word(
                    id = 1,
                    word = "academic",
                    meaning = "学术的",
                    vocabularyId = vocabulary.id,
                    category = "adj"
                ),
                Word(
                    id = 2,
                    word = "research",
                    meaning = "研究",
                    vocabularyId = vocabulary.id,
                    category = "n/v"
                )
            )
            words.forEach { wordRepository.insertWord(it) }
        }
    }

    @Test
    fun testLearningFlow() {
        // 进入学习页面
        composeTestRule.onNodeWithText("学习").performClick()

        // 选择词库
        composeTestRule.onNodeWithText("IELTS").performClick()

        // 开始学习
        composeTestRule.onNodeWithText("开始学习").performClick()

        // 第一个单词
        composeTestRule.onNodeWithText("academic").assertExists()
        composeTestRule.onNodeWithText("显示释义").performClick()
        composeTestRule.onNodeWithText("学术的").assertExists()

        // 标记认识程度
        composeTestRule.onNodeWithText("认识").performClick()

        // 第二个单词
        composeTestRule.onNodeWithText("research").assertExists()
        composeTestRule.onNodeWithText("显示释义").performClick()
        composeTestRule.onNodeWithText("研究").assertExists()

        // 标记认识程度
        composeTestRule.onNodeWithText("模糊").performClick()

        // 完成学习
        composeTestRule.onNodeWithText("完成学习").assertExists()
        composeTestRule.onNodeWithText("完成学习").performClick()

        // 检查学习报告
        composeTestRule.onNodeWithText("学习报告").assertExists()
        composeTestRule.onNodeWithText("已学习: 2").assertExists()
    }

    @Test
    fun testReviewFlow() {
        // 设置复习时间
        runBlocking {
            wordRepository.markWordAsReviewed(1, 0.8f)
            wordRepository.markWordAsReviewed(2, 0.6f)
        }

        // 进入复习页面
        composeTestRule.onNodeWithText("复习").performClick()

        // 开始复习
        composeTestRule.onNodeWithText("开始复习").performClick()

        // 第一个单词
        composeTestRule.onNodeWithText("academic").assertExists()
        composeTestRule.onNodeWithText("检查").performClick()
        composeTestRule.onNodeWithText("学术的").assertExists()

        // 评估记忆
        composeTestRule.onNodeWithText("记得很清楚").performClick()

        // 第二个单词
        composeTestRule.onNodeWithText("research").assertExists()
        composeTestRule.onNodeWithText("检查").performClick()
        composeTestRule.onNodeWithText("研究").assertExists()

        // 评估记忆
        composeTestRule.onNodeWithText("有点模糊").performClick()

        // 完成复习
        composeTestRule.onNodeWithText("完成复习").assertExists()
        composeTestRule.onNodeWithText("完成复习").performClick()

        // 检查复习报告
        composeTestRule.onNodeWithText("复习报告").assertExists()
        composeTestRule.onNodeWithText("已复习: 2").assertExists()
    }

    @Test
    fun testAIAssistance() {
        // 进入记忆页面
        composeTestRule.onNodeWithText("记忆").performClick()

        // 选择单词
        composeTestRule.onNodeWithText("academic").performClick()

        // 请求AI助手
        composeTestRule.onNodeWithText("生成记忆技巧").performClick()

        // 等待AI响应
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("memory_technique")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 保存记忆技巧
        composeTestRule.onNodeWithText("保存").performClick()

        // 验证保存成功
        composeTestRule.onNodeWithText("保存成功").assertExists()
    }

    @Test
    fun testLearningStatistics() {
        // 进行一些学习活动
        runBlocking {
            wordRepository.updateLearningProgress(1, 0.8f)
            wordRepository.updateLearningProgress(2, 0.6f)
        }

        // 查看统计
        composeTestRule.onNodeWithText("统计").performClick()

        // 验证统计数据
        composeTestRule.onNodeWithText("学习进度").assertExists()
        composeTestRule.onNodeWithText("70%").assertExists() // 平均进度
        composeTestRule.onNodeWithText("已掌握: 1").assertExists() // 进度>0.7的单词
        composeTestRule.onNodeWithText("待加强: 1").assertExists() // 进度<=0.7的单词
    }
}
