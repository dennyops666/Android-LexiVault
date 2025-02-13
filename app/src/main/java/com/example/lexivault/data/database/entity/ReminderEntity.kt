package com.example.lexivault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,           // 提醒时间（小时）
    val minute: Int,         // 提醒时间（分钟）
    val isEnabled: Boolean,  // 是否启用
    val interval: Int,       // 复习间隔（天数：1,3,7）
    val lastNotified: Long = 0  // 上次提醒时间
)
