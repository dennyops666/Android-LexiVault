package com.example.lexivault.data.converter

import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.domain.model.Word
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

class WordConverterTest {
    
    @Test
    fun `test convert entity to domain model`() {
        // Given
        val entity = WordEntity(
            id = 1L,
            word = "test",
            meaning = "测试",
            phonetic = "test",
            example = "This is a test.",
            category = "noun",
            type = "CET4",
            isBookmarked = true,
            reviewCount = 5,
            lastReviewTime = Instant.now().toEpochMilli(),
            createdAt = Instant.now().toEpochMilli()
        )

        // When
        val domain = entity.toDomainModel()

        // Then
        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.word).isEqualTo(entity.word)
        assertThat(domain.meaning).isEqualTo(entity.meaning)
        assertThat(domain.phonetic).isEqualTo(entity.phonetic)
        assertThat(domain.example).isEqualTo(entity.example)
        assertThat(domain.category).isEqualTo(entity.category)
        assertThat(domain.type).isEqualTo(entity.type)
        assertThat(domain.isBookmarked).isEqualTo(entity.isBookmarked)
        assertThat(domain.reviewCount).isEqualTo(entity.reviewCount)
        assertThat(domain.lastReviewTime).isEqualTo(entity.lastReviewTime)
        assertThat(domain.createdAt).isEqualTo(entity.createdAt)
    }

    @Test
    fun `test convert domain model to entity`() {
        // Given
        val domain = Word(
            id = 1L,
            word = "test",
            meaning = "测试",
            phonetic = "test",
            example = "This is a test.",
            category = "noun",
            type = "CET4",
            isBookmarked = true,
            reviewCount = 5,
            lastReviewTime = Instant.now().toEpochMilli(),
            createdAt = Instant.now().toEpochMilli()
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertThat(entity.id).isEqualTo(domain.id)
        assertThat(entity.word).isEqualTo(domain.word)
        assertThat(entity.meaning).isEqualTo(domain.meaning)
        assertThat(entity.phonetic).isEqualTo(domain.phonetic)
        assertThat(entity.example).isEqualTo(domain.example)
        assertThat(entity.category).isEqualTo(domain.category)
        assertThat(entity.type).isEqualTo(domain.type)
        assertThat(entity.isBookmarked).isEqualTo(domain.isBookmarked)
        assertThat(entity.reviewCount).isEqualTo(domain.reviewCount)
        assertThat(entity.lastReviewTime).isEqualTo(domain.lastReviewTime)
        assertThat(entity.createdAt).isEqualTo(domain.createdAt)
    }

    @Test
    fun `test convert null fields`() {
        // Given
        val entity = WordEntity(
            id = 1L,
            word = "test",
            meaning = "测试",
            phonetic = null,
            example = null,
            category = "noun",
            type = "CET4",
            isBookmarked = false,
            reviewCount = 0,
            lastReviewTime = 0,
            createdAt = Instant.now().toEpochMilli()
        )

        // When
        val domain = entity.toDomainModel()

        // Then
        assertThat(domain.phonetic).isNull()
        assertThat(domain.example).isNull()
    }
}
