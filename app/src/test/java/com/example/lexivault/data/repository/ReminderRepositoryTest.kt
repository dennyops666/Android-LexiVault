package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.ReminderDao
import com.example.lexivault.data.database.entity.ReminderEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalTime

class ReminderRepositoryTest {
    private lateinit var reminderDao: ReminderDao
    private lateinit var repository: ReminderRepository

    @Before
    fun setup() {
        reminderDao = mock()
        repository = ReminderRepository(reminderDao)
    }

    @Test
    fun `test getAllReminders returns all reminders from dao`() = runTest {
        // Given
        val reminders = listOf(
            ReminderEntity(
                id = 1,
                time = LocalTime.of(9, 0),
                isEnabled = true,
                type = "DAILY"
            ),
            ReminderEntity(
                id = 2,
                time = LocalTime.of(20, 0),
                isEnabled = true,
                type = "REVIEW"
            )
        )
        whenever(reminderDao.getAllReminders()).thenReturn(flowOf(reminders))

        // When
        val result = repository.getAllReminders().first()

        // Then
        assertThat(result).isEqualTo(reminders)
    }

    @Test
    fun `test updateReminderStatus updates reminder enabled status`() = runTest {
        // Given
        val reminderId = 1L
        val isEnabled = false

        // When
        repository.updateReminderStatus(reminderId, isEnabled)

        // Then
        verify(reminderDao).updateReminderStatus(reminderId, isEnabled)
    }

    @Test
    fun `test updateReminderTime updates reminder time`() = runTest {
        // Given
        val reminderId = 1L
        val newTime = LocalTime.of(10, 30)

        // When
        repository.updateReminderTime(reminderId, newTime)

        // Then
        verify(reminderDao).updateReminderTime(reminderId, newTime)
    }

    @Test
    fun `test getActiveReminders returns only enabled reminders`() = runTest {
        // Given
        val reminders = listOf(
            ReminderEntity(
                id = 1,
                time = LocalTime.of(9, 0),
                isEnabled = true,
                type = "DAILY"
            ),
            ReminderEntity(
                id = 2,
                time = LocalTime.of(20, 0),
                isEnabled = false,
                type = "REVIEW"
            )
        )
        whenever(reminderDao.getActiveReminders()).thenReturn(flowOf(reminders.filter { it.isEnabled }))

        // When
        val result = repository.getActiveReminders().first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().id).isEqualTo(1)
    }
}
