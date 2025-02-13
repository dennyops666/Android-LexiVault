package com.example.lexivault.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.lexivault.data.repository.ReminderRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import com.google.common.truth.Truth.assertThat

@RunWith(RobolectricTestRunner::class)
class ReminderWorkerTest {
    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters
    private lateinit var reminderRepository: ReminderRepository
    private lateinit var worker: TestReminderWorker

    @Before
    fun setup() {
        context = mock()
        workerParams = mock()
        reminderRepository = mock()
        worker = TestReminderWorker(context, workerParams, reminderRepository)
    }

    @Test
    fun `test doWork returns success when repository call succeeds`() = runTest {
        // Given
        whenever(reminderRepository.checkAndSendNotifications()).thenReturn(Unit)

        // When
        val result = worker.doWork()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        verify(reminderRepository).checkAndSendNotifications()
    }

    @Test
    fun `test doWork returns retry when repository call fails`() = runTest {
        // Given
        whenever(reminderRepository.checkAndSendNotifications()).thenThrow(RuntimeException("Test exception"))

        // When
        val result = worker.doWork()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        verify(reminderRepository).checkAndSendNotifications()
    }
}
