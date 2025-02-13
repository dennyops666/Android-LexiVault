package com.example.lexivault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "test_records")
data class TestRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val testType: TestType,
    val isCorrect: Boolean,
    val userAnswer: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class TestType {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK
}
