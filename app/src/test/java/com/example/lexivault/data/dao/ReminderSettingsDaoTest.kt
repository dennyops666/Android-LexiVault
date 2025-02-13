package com.example.lexivault.data.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.lexivault.data.database.AppDatabase
import com.example.lexivault.data.entity.ReminderSettings
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
class ReminderSettingsDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderSettingsDao: ReminderSettingsDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        reminderSettingsDao = db.reminderSettingsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetReminderSettings() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )

        // When
        reminderSettingsDao.insertReminderSettings(settings)
        val result = reminderSettingsDao.getReminderSettings(1).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result.enabled).isTrue()
        assertThat(result.reminderTime).isEqualTo(LocalTime.of(20, 0))
        assertThat(result.daysOfWeek).containsExactly(1, 2, 3, 4, 5)
    }

    @Test
    fun updateReminderSettings() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )
        reminderSettingsDao.insertReminderSettings(settings)

        // When
        val updatedSettings = settings.copy(
            enabled = false,
            reminderTime = LocalTime.of(21, 0)
        )
        reminderSettingsDao.updateReminderSettings(updatedSettings)
        val result = reminderSettingsDao.getReminderSettings(1).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result.enabled).isFalse()
        assertThat(result.reminderTime).isEqualTo(LocalTime.of(21, 0))
    }

    @Test
    fun deleteReminderSettings() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )
        reminderSettingsDao.insertReminderSettings(settings)

        // When
        reminderSettingsDao.deleteReminderSettings(settings)
        val result = reminderSettingsDao.getReminderSettings(1).first()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun enableDisableReminders() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )
        reminderSettingsDao.insertReminderSettings(settings)

        // When
        reminderSettingsDao.updateReminderEnabled(1, false)
        val result = reminderSettingsDao.getReminderSettings(1).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result.enabled).isFalse()
    }

    @Test
    fun updateReminderTime() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )
        reminderSettingsDao.insertReminderSettings(settings)

        // When
        val newTime = LocalTime.of(22, 30)
        reminderSettingsDao.updateReminderTime(1, newTime)
        val result = reminderSettingsDao.getReminderSettings(1).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result.reminderTime).isEqualTo(newTime)
    }

    @Test
    fun updateReminderDays() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )
        reminderSettingsDao.insertReminderSettings(settings)

        // When
        val newDays = listOf(6, 7) // 周末
        reminderSettingsDao.updateReminderDays(1, newDays)
        val result = reminderSettingsDao.getReminderSettings(1).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result.daysOfWeek).containsExactly(6, 7)
    }
}
