package com.example.lexivault.ui.interaction

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
class ComplexInteractionTest {

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
    fun testAddWordFlow() {
        // 点击添加按钮
        composeTestRule.onNodeWithContentDescription("添加单词").performClick()

        // 填写单词信息
        composeTestRule.onNodeWithTag("word_input").performTextInput("test")
        composeTestRule.onNodeWithTag("meaning_input").performTextInput("测试")
        composeTestRule.onNodeWithTag("category_dropdown").performClick()
        composeTestRule.onNodeWithText("名词").performClick()
        composeTestRule.onNodeWithTag("type_dropdown").performClick()
        composeTestRule.onNodeWithText("CET4").performClick()

        // 提交
        composeTestRule.onNodeWithText("保存").performClick()

        // 验证单词已添加到列表
        composeTestRule.onNodeWithText("test").assertIsDisplayed()
        composeTestRule.onNodeWithText("测试").assertIsDisplayed()
    }

    @Test
    fun testWordReviewFlow() {
        // 进入复习界面
        composeTestRule.onNodeWithText("复习").performClick()

        // 开始复习
        composeTestRule.onNodeWithText("开始复习").performClick()

        // 展示单词卡片
        composeTestRule.onNodeWithTag("word_card").assertIsDisplayed()

        // 点击显示答案
        composeTestRule.onNodeWithText("显示答案").performClick()

        // 验证答案已显示
        composeTestRule.onNodeWithTag("word_meaning").assertIsDisplayed()

        // 评价难度
        composeTestRule.onNodeWithTag("difficulty_easy").performClick()

        // 验证进入下一个单词
        composeTestRule.onNodeWithTag("word_card").assertExists()
    }

    @Test
    fun testSearchAndFilter() {
        // 点击搜索框
        composeTestRule.onNodeWithTag("search_bar").performClick()

        // 输入搜索关键词
        composeTestRule.onNodeWithTag("search_input").performTextInput("test")

        // 验证搜索结果
        composeTestRule.onNodeWithTag("search_results").assertIsDisplayed()

        // 应用过滤器
        composeTestRule.onNodeWithTag("filter_button").performClick()
        composeTestRule.onNodeWithText("CET4").performClick()
        composeTestRule.onNodeWithText("应用").performClick()

        // 验证过滤结果
        composeTestRule.onNodeWithTag("filtered_results").assertIsDisplayed()
    }

    @Test
    fun testDragAndDropReorder() {
        // 长按第一个单词卡片
        composeTestRule.onNodeWithTag("word_item_0")
            .performTouchInput { longClick() }

        // 拖动到新位置
        composeTestRule.onNodeWithTag("word_item_0")
            .performTouchInput { 
                swipeDown(endY = 200f)
            }

        // 验证顺序已更改
        composeTestRule.onNodeWithTag("word_list")
            .assertIsDisplayed()
    }

    @Test
    fun testGestureNavigation() {
        // 从左向右滑动返回
        composeTestRule.onNodeWithTag("main_screen")
            .performTouchInput {
                swipeRight()
            }

        // 验证返回到上一页
        composeTestRule.onNodeWithTag("previous_screen")
            .assertIsDisplayed()

        // 从右向左滑动前进
        composeTestRule.onNodeWithTag("main_screen")
            .performTouchInput {
                swipeLeft()
            }

        // 验证前进到下一页
        composeTestRule.onNodeWithTag("next_screen")
            .assertIsDisplayed()
    }

    @Test
    fun testMultiSelectAndBatchOperation() {
        // 长按启动多选模式
        composeTestRule.onNodeWithTag("word_item_0")
            .performTouchInput { longClick() }

        // 选择多个项目
        composeTestRule.onNodeWithTag("word_item_1").performClick()
        composeTestRule.onNodeWithTag("word_item_2").performClick()

        // 执行批量操作
        composeTestRule.onNodeWithTag("batch_delete").performClick()

        // 确认删除
        composeTestRule.onNodeWithText("确认").performClick()

        // 验证项目已删除
        composeTestRule.onNodeWithTag("word_item_0").assertDoesNotExist()
        composeTestRule.onNodeWithTag("word_item_1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("word_item_2").assertDoesNotExist()
    }
}
