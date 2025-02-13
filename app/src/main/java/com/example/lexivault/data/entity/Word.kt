package com.example.lexivault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = Vocabulary::class,
            parentColumns = ["id"],
            childColumns = ["vocabulary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("vocabulary_id")
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "vocabulary_id")
    val vocabularyId: Long,

    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "pronunciation")
    val pronunciation: String,

    @ColumnInfo(name = "meaning")
    val meaning: String,

    @ColumnInfo(name = "example")
    val example: String,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)
