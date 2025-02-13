package com.example.lexivault.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.data.database.AppDatabase
import com.example.lexivault.data.database.entity.ReminderEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ReminderDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var reminderDao: ReminderDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        reminderDao = database.reminderDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testInsertAndGetReminder() = runTest {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )

        // When
        reminderDao.insert(reminder)
        val allReminders = reminderDao.getAllReminders().first()

        // Then
        assertThat(allReminders).contains(reminder)
    }

    @Test
    fun testUpdateReminderStatus() = runTest {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        reminderDao.insert(reminder)

        // When
        reminderDao.updateReminderStatus(1, false)
        val updatedReminder = reminderDao.getAllReminders().first().first()

        // Then
        assertThat(updatedReminder.isEnabled).isFalse()
    }

    @Test
    fun testUpdateReminderTime() = runTest {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        reminderDao.insert(reminder)

        // When
        val newTime = LocalTime.of(10, 30)
        reminderDao.updateReminderTime(1, newTime)
        val updatedReminder = reminderDao.getAllReminders().first().first()

        // Then
        assertThat(updatedReminder.time).isEqualTo(newTime)
    }

    @Test
    fun testDeleteReminder() = runTest {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        reminderDao.insert(reminder)

        // When
        reminderDao.delete(reminder)
        val allReminders = reminderDao.getAllReminders().first()

        // Then
        assertThat(allReminders).isEmpty()
    }

    @Test
    fun testGetActiveReminders() = runTest {
        // Given
        val activeReminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        val inactiveReminder = ReminderEntity(
            id = 2,
            time = LocalTime.of(15, 0),
            isEnabled = false,
            type = "REVIEW"
        )
        reminderDao.insert(activeReminder)
        reminderDao.insert(inactiveReminder)

        // When
        val activeReminders = reminderDao.getActiveReminders().first()

        // Then
        assertThat(activeReminders).hasSize(1)
        assertThat(activeReminders.first().id).isEqualTo(1)
    }
}
