package com.example.lexivault.feature

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import com.example.lexivault.data.network.api.OpenAIService
import com.example.lexivault.data.repository.AIRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AIFeatureTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var aiRepository: AIRepository

    private lateinit var openAIService: OpenAIService

    @Before
    fun setup() {
        hiltRule.inject()
        openAIService = mockk()
    }

    @Test
    fun testWordAssociationMemory() {
        // 进入单词详情
        composeTestRule.onNodeWithText("test").performClick()
        
        // 请求AI联想记忆
        composeTestRule.onNodeWithText("AI记忆法").performClick()
        
        // 模拟AI响应
        coEvery { openAIService.generateMemoryTip(any()) } returns 
            "test（测试）可以联想为：'特殊的考验'。把'test'拆解为't-特殊'+'est-考验'，形成记忆链接。"
        
        // 验证联想记忆显示
        composeTestRule.onNodeWithTag("memory_tip")
            .assertTextContains("特殊的考验")
    }

    @Test
    fun testAIExampleGeneration() {
        // 进入单词详情
        composeTestRule.onNodeWithText("test").performClick()
        
        // 请求AI生成例句
        composeTestRule.onNodeWithText("生成例句").performClick()
        
        // 模拟AI响应
        coEvery { openAIService.generateExample(any()) } returns 
            "The scientist needed to test his hypothesis in the laboratory."
        
        // 验证例句显示
        composeTestRule.onNodeWithTag("example_sentence")
            .assertTextContains("scientist")
    }

    @Test
    fun testSmartWordRecommendation() {
        // 进入推荐页面
        composeTestRule.onNodeWithText("智能推荐").performClick()
        
        // 模拟AI推荐响应
        coEvery { openAIService.getWordRecommendations(any()) } returns 
            listOf("analyze", "hypothesis", "experiment")
        
        // 验证推荐列表
        composeTestRule.onNodeWithTag("recommendation_list")
            .assertExists()
        composeTestRule.onNodeWithText("analyze")
            .assertExists()
    }

    @Test
    fun testAIErrorHandling() {
        // 进入单词详情
        composeTestRule.onNodeWithText("test").performClick()
        
        // 模拟AI服务错误
        coEvery { openAIService.generateMemoryTip(any()) } throws 
            Exception("API Error")
        
        // 请求AI联想记忆
        composeTestRule.onNodeWithText("AI记忆法").performClick()
        
        // 验证错误提示
        composeTestRule.onNodeWithTag("error_message")
            .assertTextContains("生成失败")
    }

    @Test
    fun testAIResponseCaching() = runBlocking {
        // 首次请求AI联想记忆
        val word = "test"
        val memoryTip = "test的记忆技巧"
        
        coEvery { openAIService.generateMemoryTip(word) } returns memoryTip
        
        val firstResponse = aiRepository.getMemoryTip(word)
        assert(firstResponse == memoryTip)
        
        // 修改模拟响应
        coEvery { openAIService.generateMemoryTip(word) } returns 
            "新的记忆技巧"
        
        // 验证缓存结果
        val cachedResponse = aiRepository.getMemoryTip(word)
        assert(cachedResponse == memoryTip)
    }

    @Test
    fun testAIConfigurationUpdate() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()
        
        // 更新AI设置
        composeTestRule.onNodeWithText("AI设置").performClick()
        composeTestRule.onNodeWithTag("temperature_slider")
            .performTouchInput { 
                swipeRight()
            }
        composeTestRule.onNodeWithText("保存").performClick()
        
        // 验证设置已更新
        runBlocking {
            val config = aiRepository.getAIConfig()
            assert(config.temperature > 0.5f)
        }
    }
}
