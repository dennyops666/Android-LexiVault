package com.example.lexivault.util

import com.example.lexivault.data.entity.Vocabulary
import com.example.lexivault.data.entity.Word
import java.time.LocalDateTime

object TestUtil {
    fun createVocabulary(
        id: Long = 1L,
        name: String = "CET4",
        description: String = "CET4 vocabulary list",
        createdAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0),
        updatedAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0)
    ) = Vocabulary(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun createWord(
        id: Long = 1L,
        vocabularyId: Long = 1L,
        word: String = "test",
        pronunciation: String = "test",
        meaning: String = "test",
        example: String = "This is a test.",
        createdAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0),
        updatedAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0)
    ) = Word(
        id = id,
        vocabularyId = vocabularyId,
        word = word,
        pronunciation = pronunciation,
        meaning = meaning,
        example = example,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
