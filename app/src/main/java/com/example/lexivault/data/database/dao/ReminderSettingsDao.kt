package com.example.lexivault.data.database.dao

import androidx.room.*
import com.example.lexivault.data.database.entity.ReminderSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderSettingsDao {
    @Query("SELECT * FROM reminder_settings ORDER BY createdAt DESC LIMIT 1")
    fun getLatestSettings(): Flow<ReminderSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: ReminderSettingsEntity)

    @Update
    suspend fun update(settings: ReminderSettingsEntity)

    @Delete
    suspend fun delete(settings: ReminderSettingsEntity)

    @Query("DELETE FROM reminder_settings")
    suspend fun deleteAll()
} 