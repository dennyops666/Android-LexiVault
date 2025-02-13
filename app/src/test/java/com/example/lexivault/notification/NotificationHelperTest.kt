package com.example.lexivault.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

class NotificationHelperTest {
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationHelper: NotificationHelper

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = mock()
        whenever(context.getSystemService(Context.NOTIFICATION_SERVICE))
            .thenReturn(notificationManager)
        notificationHelper = NotificationHelper(context)
    }

    @Test
    fun `test createNotificationChannel creates channel with correct settings`() {
        // Given
        val channelId = "learning_reminder_channel"
        val channelName = "Learning Reminders"
        val channelDescription = "Notifications for daily learning reminders"

        // When
        notificationHelper.createNotificationChannel()

        // Then
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verify(notificationManager).createNotificationChannel(
                argThat { channel ->
                    channel.id == channelId &&
                    channel.name == channelName &&
                    channel.description == channelDescription &&
                    channel.importance == NotificationManager.IMPORTANCE_HIGH
                }
            )
        }
    }

    @Test
    fun `test showLearningReminder shows notification with correct content`() {
        // When
        notificationHelper.showLearningReminder()

        // Then
        verify(notificationManager).notify(
            eq(NotificationHelper.REMINDER_NOTIFICATION_ID),
            argThat { notification ->
                notification.extras.getString("android.title") == "该学习了！" &&
                notification.extras.getString("android.text")?.contains("今天的学习目标") == true
            }
        )
    }

    @Test
    fun `test showTestReminder shows notification with correct content`() {
        // When
        notificationHelper.showTestReminder()

        // Then
        verify(notificationManager).notify(
            eq(NotificationHelper.TEST_NOTIFICATION_ID),
            argThat { notification ->
                notification.extras.getString("android.title") == "测试时间到了！" &&
                notification.extras.getString("android.text")?.contains("来测试一下") == true
            }
        )
    }

    @Test
    fun `test cancelNotification cancels notification`() {
        // When
        notificationHelper.cancelNotification(NotificationHelper.REMINDER_NOTIFICATION_ID)

        // Then
        verify(notificationManager).cancel(NotificationHelper.REMINDER_NOTIFICATION_ID)
    }

    @Test
    fun `test cancelAllNotifications cancels all notifications`() {
        // When
        notificationHelper.cancelAllNotifications()

        // Then
        verify(notificationManager).cancelAll()
    }
}
