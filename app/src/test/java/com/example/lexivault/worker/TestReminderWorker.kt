package com.example.lexivault.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lexivault.data.repository.ReminderRepository

class TestReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            reminderRepository.checkAndSendNotifications()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
