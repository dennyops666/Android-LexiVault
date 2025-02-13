package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.ReminderDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.ReminderEntity
import com.example.lexivault.data.database.entity.ReminderType
import com.example.lexivault.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao,
    private val wordDao: WordDao,
    private val notificationHelper: NotificationHelper
) {
    fun getDueReminders(): Flow<List<ReminderEntity>> {
        return reminderDao.getDueReminders(LocalDateTime.now())
    }

    suspend fun createReviewReminders(wordId: Long) {
        val word = wordDao.getWordById(wordId) ?: return

        // 创建1天、3天、7天后的复习提醒
        val now = LocalDateTime.now()
        val reminders = listOf(
            ReminderEntity(
                wordId = wordId,
                reminderTime = now.plusDays(1),
                reminderType = ReminderType.DAY_1
            ),
            ReminderEntity(
                wordId = wordId,
                reminderTime = now.plusDays(3),
                reminderType = ReminderType.DAY_3
            ),
            ReminderEntity(
                wordId = wordId,
                reminderTime = now.plusDays(7),
                reminderType = ReminderType.DAY_7
            )
        )

        reminders.forEach { reminder ->
            reminderDao.insert(reminder)
        }
    }

    suspend fun markReminderAsCompleted(reminderId: Long) {
        reminderDao.markReminderAsCompleted(reminderId)
    }

    suspend fun checkAndSendNotifications() {
        val now = LocalDateTime.now()
        val reminders = reminderDao.getDueReminders(now).first()
        val words = wordDao.getAllWords().first()
        val wordMap = words.associateBy { it.id }
        
        reminders.forEach { reminder ->
            val word = wordMap[reminder.wordId] ?: return@forEach
            val reminderType = when (reminder.reminderType) {
                ReminderType.DAILY -> "每日学习"
                ReminderType.DAY_1 -> "1天复习"
                ReminderType.DAY_3 -> "3天复习"
                ReminderType.DAY_7 -> "7天复习"
            }
            notificationHelper.showReviewReminder(word.word, reminderType)
            markReminderAsCompleted(reminder.id)
        }
    }

    suspend fun setDailyReminder(hour: Int, minute: Int) {
        // 创建每日提醒
        val now = LocalDateTime.now()
        val reminderTime = now.withHour(hour).withMinute(minute)
        val actualReminderTime = if (reminderTime.isBefore(now)) {
            reminderTime.plusDays(1)
        } else {
            reminderTime
        }

        reminderDao.insert(
            ReminderEntity(
                wordId = -1, // 使用特殊ID表示每日提醒
                reminderTime = actualReminderTime,
                reminderType = ReminderType.DAILY
            )
        )
    }

    suspend fun cancelDailyReminder() {
        // 删除所有每日提醒
        val reminders = reminderDao.getDueReminders(LocalDateTime.now()).first()
        reminders.filter { it.reminderType == ReminderType.DAILY }
            .forEach { reminderDao.delete(it) }
    }
}
