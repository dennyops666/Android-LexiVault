package com.example.lexivault.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import com.example.lexivault.data.repository.ReminderRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReminderSettingsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testReminderSetup() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 进入提醒设置
        composeTestRule.onNodeWithText("提醒设置").performClick()

        // 启用提醒
        composeTestRule.onNodeWithTag("reminder_switch")
            .performClick()

        // 设置提醒时间
        composeTestRule.onNodeWithTag("time_picker").performClick()
        // 选择时间 20:00
        composeTestRule.onNodeWithTag("hour_picker")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText("20").performClick()
        composeTestRule.onNodeWithTag("minute_picker")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText("00").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 选择重复日期
        composeTestRule.onNodeWithTag("weekday_selector").performClick()
        composeTestRule.onNodeWithText("周一").performClick()
        composeTestRule.onNodeWithText("周三").performClick()
        composeTestRule.onNodeWithText("周五").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 保存设置
        composeTestRule.onNodeWithText("保存").performClick()

        // 验证保存成功
        composeTestRule.onNodeWithText("设置已保存").assertExists()

        // 验证设置已生效
        composeTestRule.onNodeWithText("20:00").assertExists()
        composeTestRule.onNodeWithText("周一, 周三, 周五").assertExists()
    }

    @Test
    fun testReminderNotification() {
        // 设置测试提醒
        val currentTime = LocalTime.now()
        val testTime = currentTime.plusMinutes(1)

        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 进入提醒设置
        composeTestRule.onNodeWithText("提醒设置").performClick()

        // 启用提醒
        composeTestRule.onNodeWithTag("reminder_switch")
            .performClick()

        // 设置提醒时间为当前时间后1分钟
        composeTestRule.onNodeWithTag("time_picker").performClick()
        composeTestRule.onNodeWithTag("hour_picker")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText(testTime.hour.toString()).performClick()
        composeTestRule.onNodeWithTag("minute_picker")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText(testTime.minute.toString()).performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 选择今天
        composeTestRule.onNodeWithTag("weekday_selector").performClick()
        val today = java.time.LocalDateTime.now().dayOfWeek.value
        composeTestRule.onNodeWithTag("day_$today").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 保存设置
        composeTestRule.onNodeWithText("保存").performClick()

        // 等待提醒触发
        Thread.sleep(70000) // 等待70秒

        // 验证提醒已触发
        composeTestRule.onNodeWithText("该复习单词了").assertExists()
    }

    @Test
    fun testReminderDisable() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 进入提醒设置
        composeTestRule.onNodeWithText("提醒设置").performClick()

        // 启用提醒
        composeTestRule.onNodeWithTag("reminder_switch")
            .performClick()

        // 设置提醒
        composeTestRule.onNodeWithTag("time_picker").performClick()
        composeTestRule.onNodeWithText("20").performClick()
        composeTestRule.onNodeWithText("00").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 保存设置
        composeTestRule.onNodeWithText("保存").performClick()

        // 禁用提醒
        composeTestRule.onNodeWithTag("reminder_switch")
            .performClick()

        // 保存设置
        composeTestRule.onNodeWithText("保存").performClick()

        // 验证提醒已禁用
        composeTestRule.onNodeWithText("提醒已禁用").assertExists()
    }

    @Test
    fun testReminderFrequencyChange() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()

        // 进入提醒设置
        composeTestRule.onNodeWithText("提醒设置").performClick()

        // 启用提醒
        composeTestRule.onNodeWithTag("reminder_switch")
            .performClick()

        // 设置每日提醒
        composeTestRule.onNodeWithTag("frequency_selector").performClick()
        composeTestRule.onNodeWithText("每日").performClick()

        // 设置提醒时间
        composeTestRule.onNodeWithTag("time_picker").performClick()
        composeTestRule.onNodeWithText("20").performClick()
        composeTestRule.onNodeWithText("00").performClick()
        composeTestRule.onNodeWithText("确认").performClick()

        // 保存设置
        composeTestRule.onNodeWithText("保存").performClick()

        // 验证设置已保存
        composeTestRule.onNodeWithText("每日 20:00").assertExists()

        // 更改为工作日提醒
        composeTestRule.onNodeWithTag("frequency_selector").performClick()
        composeTestRule.onNodeWithText("工作日").performClick()

        // 保存设置
        composeTestRule.onNodeWithText("保存").performClick()

        // 验证设置已更新
        composeTestRule.onNodeWithText("工作日 20:00").assertExists()
    }
}
