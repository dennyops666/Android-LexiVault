package com.example.lexivault.data.database.dao

import androidx.room.*
import com.example.lexivault.data.database.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: ReminderEntity)

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1")
    fun getActiveReminders(): Flow<List<ReminderEntity>>

    @Query("UPDATE reminders SET lastNotified = :time WHERE id = :id")
    suspend fun updateLastNotified(id: Long, time: Long)

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateReminderStatus(id: Long, enabled: Boolean)
}
