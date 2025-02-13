package com.example.lexivault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "reminder_settings")
data class ReminderSettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reminderTime: LocalTime,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 