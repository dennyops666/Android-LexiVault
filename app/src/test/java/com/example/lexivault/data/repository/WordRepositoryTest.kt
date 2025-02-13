package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.WordEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class WordRepositoryTest {
    private lateinit var wordDao: WordDao
    private lateinit var repository: WordRepository

    @Before
    fun setup() {
        wordDao = mock()
        repository = WordRepository(wordDao)
    }

    @Test
    fun `test getAllWords returns all words from dao`() = runTest {
        // Given
        val words = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4"
            ),
            WordEntity(
                id = 2,
                word = "example",
                meaning = "例子",
                category = "noun",
                type = "CET4"
            )
        )
        whenever(wordDao.getAllWords()).thenReturn(flowOf(words))

        // When
        val result = repository.getAllWords().first()

        // Then
        assertThat(result).isEqualTo(words)
    }

    @Test
    fun `test searchWords returns matching words`() = runTest {
        // Given
        val query = "test"
        val matchingWords = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4"
            ),
            WordEntity(
                id = 2,
                word = "testing",
                meaning = "测试中",
                category = "noun",
                type = "CET4"
            )
        )
        whenever(wordDao.searchWords("%$query%")).thenReturn(flowOf(matchingWords))

        // When
        val result = repository.searchWords(query).first()

        // Then
        assertThat(result).isEqualTo(matchingWords)
    }

    @Test
    fun `test updateBookmarkStatus updates word bookmark status`() = runTest {
        // Given
        val wordId = 1L
        val isBookmarked = true

        // When
        repository.updateBookmarkStatus(wordId, isBookmarked)

        // Then
        verify(wordDao).updateBookmarkStatus(wordId, isBookmarked)
    }

    @Test
    fun `test getWordsByCategory returns words of specified category`() = runTest {
        // Given
        val category = "noun"
        val nouns = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4"
            ),
            WordEntity(
                id = 2,
                word = "example",
                meaning = "例子",
                category = "noun",
                type = "CET4"
            )
        )
        whenever(wordDao.getWordsByCategory(category)).thenReturn(flowOf(nouns))

        // When
        val result = repository.getWordsByCategory(category).first()

        // Then
        assertThat(result).isEqualTo(nouns)
    }

    @Test
    fun `test getBookmarkedWords returns only bookmarked words`() = runTest {
        // Given
        val bookmarkedWords = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4",
                isBookmarked = true
            )
        )
        whenever(wordDao.getBookmarkedWords()).thenReturn(flowOf(bookmarkedWords))

        // When
        val result = repository.getBookmarkedWords().first()

        // Then
        assertThat(result).isEqualTo(bookmarkedWords)
    }
}
