package com.example.lexivault.ui.screens.settings

import com.example.lexivault.data.database.entity.ReminderEntity
import com.example.lexivault.data.repository.ReminderRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderSettingsViewModelTest {
    private lateinit var reminderRepository: ReminderRepository
    private lateinit var viewModel: ReminderSettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reminderRepository = mock()
        viewModel = ReminderSettingsViewModel(reminderRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadReminders loads all reminders`() = runTest {
        // Given
        val reminders = listOf(
            ReminderEntity(id = 1, time = LocalTime.of(9, 0), isEnabled = true, type = "DAILY"),
            ReminderEntity(id = 2, time = LocalTime.of(20, 0), isEnabled = false, type = "REVIEW")
        )
        whenever(reminderRepository.getAllReminders()).thenReturn(flowOf(reminders))

        // When
        viewModel.loadReminders()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.reminders).isEqualTo(reminders)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `test updateReminderTime updates reminder time`() = runTest {
        // Given
        val reminderId = 1L
        val newTime = LocalTime.of(10, 30)

        // When
        viewModel.updateReminderTime(reminderId, newTime)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(reminderRepository).updateReminderTime(reminderId, newTime)
    }

    @Test
    fun `test toggleReminder updates reminder status`() = runTest {
        // Given
        val reminderId = 1L
        val isEnabled = true

        // When
        viewModel.toggleReminder(reminderId, isEnabled)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(reminderRepository).updateReminderStatus(reminderId, isEnabled)
    }

    @Test
    fun `test addReminder adds new reminder`() = runTest {
        // Given
        val newReminder = ReminderEntity(
            id = 0,
            time = LocalTime.of(15, 0),
            isEnabled = true,
            type = "DAILY"
        )

        // When
        viewModel.addReminder(newReminder)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(reminderRepository).insertReminder(newReminder)
    }

    @Test
    fun `test deleteReminder removes reminder`() = runTest {
        // Given
        val reminderId = 1L

        // When
        viewModel.deleteReminder(reminderId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(reminderRepository).deleteReminder(reminderId)
    }

    @Test
    fun `test initial state is correct`() {
        // When initial state
        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.reminders).isEmpty()
        assertThat(initialState.error).isNull()
    }

    @Test
    fun `test error handling`() = runTest {
        // Given
        val errorMessage = "Failed to load reminders"
        whenever(reminderRepository.getAllReminders()).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.loadReminders()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.error).isEqualTo(errorMessage)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }
}
