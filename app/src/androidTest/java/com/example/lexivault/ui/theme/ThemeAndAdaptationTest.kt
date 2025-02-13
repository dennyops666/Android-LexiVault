package com.example.lexivault.ui.theme

import android.content.pm.ActivityInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lexivault.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemeAndAdaptationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testDarkModeSwitch() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 切换到深色模式
        composeTestRule.onNodeWithTag("theme_switch").performClick()

        // 验证深色模式已应用
        composeTestRule.onNodeWithTag("main_screen")
            .assertBackgroundColor(isDark = true)

        // 验证文本颜色已适应深色模式
        composeTestRule.onNodeWithTag("text_primary")
            .assertTextColor(isDark = true)

        // 切换回浅色模式
        composeTestRule.onNodeWithTag("theme_switch").performClick()

        // 验证浅色模式已应用
        composeTestRule.onNodeWithTag("main_screen")
            .assertBackgroundColor(isDark = false)
    }

    @Test
    fun testDynamicColors() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 启用动态颜色
        composeTestRule.onNodeWithTag("dynamic_colors_switch").performClick()

        // 验证动态颜色已应用
        composeTestRule.onNodeWithTag("main_screen")
            .assertHasDynamicColors()

        // 禁用动态颜色
        composeTestRule.onNodeWithTag("dynamic_colors_switch").performClick()

        // 验证已恢复默认颜色
        composeTestRule.onNodeWithTag("main_screen")
            .assertHasDefaultColors()
    }

    @Test
    fun testScreenRotation() {
        // 添加一个单词
        composeTestRule.onNodeWithContentDescription("添加单词").performClick()
        composeTestRule.onNodeWithTag("word_input").performTextInput("test")
        composeTestRule.onNodeWithTag("meaning_input").performTextInput("测试")
        
        // 旋转屏幕到横屏
        composeTestRule.activity.requestedOrientation = 
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // 验证数据保持
        composeTestRule.onNodeWithTag("word_input")
            .assertTextEquals("test")
        composeTestRule.onNodeWithTag("meaning_input")
            .assertTextEquals("测试")

        // 验证布局适应
        composeTestRule.onNodeWithTag("form_container")
            .assertIsDisplayed()
            .assertHeightIsAtLeast(100.dp)

        // 旋转回竖屏
        composeTestRule.activity.requestedOrientation = 
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // 再次验证数据保持
        composeTestRule.onNodeWithTag("word_input")
            .assertTextEquals("test")
        composeTestRule.onNodeWithTag("meaning_input")
            .assertTextEquals("测试")
    }

    @Test
    fun testFontScaling() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 调整字体大小
        composeTestRule.onNodeWithTag("font_scale_slider")
            .performTouchInput {
                swipeRight()
            }

        // 验证文本大小已更改
        composeTestRule.onNodeWithTag("sample_text")
            .assertFontScale(1.5f)

        // 恢复默认字体大小
        composeTestRule.onNodeWithTag("font_scale_slider")
            .performTouchInput {
                swipeLeft()
            }

        // 验证文本大小已恢复
        composeTestRule.onNodeWithTag("sample_text")
            .assertFontScale(1.0f)
    }

    @Test
    fun testResponsiveLayout() {
        // 验证不同屏幕尺寸下的布局适应
        val density = composeTestRule.density
        val screenWidth = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .resources
            .displayMetrics
            .widthPixels / density.density

        if (screenWidth >= 600) {
            // 平板布局
            composeTestRule.onNodeWithTag("tablet_layout")
                .assertIsDisplayed()
            composeTestRule.onNodeWithTag("navigation_rail")
                .assertIsDisplayed()
        } else {
            // 手机布局
            composeTestRule.onNodeWithTag("phone_layout")
                .assertIsDisplayed()
            composeTestRule.onNodeWithTag("bottom_navigation")
                .assertIsDisplayed()
        }
    }

    @Test
    fun testAccessibility() {
        // 验证颜色对比度
        composeTestRule.onNodeWithTag("primary_button")
            .assertHasAdequateColorContrast()

        // 验证可点击区域大小
        composeTestRule.onNodeWithTag("action_button")
            .assertHasTouchTargetSize()

        // 验证内容描述
        composeTestRule.onNodeWithTag("word_card")
            .assertHasContentDescription()
    }
}
