package com.example.lexivault.feature

import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.MainActivity
import com.example.lexivault.data.repository.ReminderRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReviewReminderTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var reminderRepository: ReminderRepository

    private lateinit var notificationManager: NotificationManager

    @Before
    fun setup() {
        hiltRule.inject()
        notificationManager = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Test
    fun testDailyReminderSetting() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()
        
        // 启用每日提醒
        composeTestRule.onNodeWithText("每日提醒").performClick()
        
        // 设置提醒时间
        composeTestRule.onNodeWithTag("time_picker").performClick()
        composeTestRule.onNodeWithText("09").performClick()
        composeTestRule.onNodeWithText("00").performClick()
        composeTestRule.onNodeWithText("确定").performClick()
        
        // 验证提醒设置已保存
        runBlocking {
            val reminder = reminderRepository.getDailyReminder()
            assert(reminder.isEnabled)
            assert(reminder.time == LocalTime.of(9, 0))
        }
    }

    @Test
    fun testReminderNotification() = runBlocking {
        // 设置提醒时间为当前时间后1分钟
        val reminderTime = LocalTime.now().plusMinutes(1)
        reminderRepository.setDailyReminder(true, reminderTime)
        
        // 等待提醒触发
        delay(70_000) // 等待70秒
        
        // 验证通知是否发送
        val notifications = notificationManager.activeNotifications
        assert(notifications.any { it.notification.channelId == "daily_reminder" })
    }

    @Test
    fun testSmartReviewPlan() {
        // 进入复习计划页面
        composeTestRule.onNodeWithText("复习计划").performClick()
        
        // 验证复习计划显示
        composeTestRule.onNodeWithTag("review_plan").assertExists()
        
        // 验证不同时间间隔的复习任务
        composeTestRule.onNodeWithTag("day1_review").assertExists()
        composeTestRule.onNodeWithTag("day3_review").assertExists()
        composeTestRule.onNodeWithTag("day7_review").assertExists()
    }

    @Test
    fun testReviewReminder() = runBlocking {
        // 添加需要复习的单词
        val word = testWordEntity.copy(lastReviewTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000)
        wordRepository.insertWord(word.toDomainModel())
        
        // 触发复习检查
        reminderRepository.checkAndScheduleReviews()
        
        // 验证复习提醒
        delay(1000)
        val notifications = notificationManager.activeNotifications
        assert(notifications.any { it.notification.channelId == "review_reminder" })
    }

    @Test
    fun testCustomReviewSchedule() {
        // 进入设置页面
        composeTestRule.onNodeWithText("设置").performClick()
        
        // 自定义复习计划
        composeTestRule.onNodeWithText("自定义复习计划").performClick()
        
        // 修改复习间隔
        composeTestRule.onNodeWithTag("day1_interval")
            .performTextReplacement("2")
        composeTestRule.onNodeWithTag("day3_interval")
            .performTextReplacement("5")
        composeTestRule.onNodeWithTag("day7_interval")
            .performTextReplacement("10")
        
        composeTestRule.onNodeWithText("保存").performClick()
        
        // 验证设置已保存
        runBlocking {
            val schedule = reminderRepository.getReviewSchedule()
            assert(schedule.firstReview == 2)
            assert(schedule.secondReview == 5)
            assert(schedule.thirdReview == 10)
        }
    }
}
