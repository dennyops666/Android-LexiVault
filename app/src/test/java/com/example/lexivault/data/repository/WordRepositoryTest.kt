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

    @Test
    fun `TC-8_1_1 test insertWord successfully inserts a word`() = runTest {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4"
        )

        // When
        repository.insertWord(word)

        // Then
        verify(wordDao).insert(word)
    }

    @Test
    fun `TC-8_1_2 test getWordById successfully retrieves a word`() = runTest {
        // Given
        val wordId = 1L
        val word = WordEntity(
            id = wordId,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4"
        )
        whenever(wordDao.getWordById(wordId)).thenReturn(word)

        // When
        val result = repository.getWordById(wordId)

        // Then
        assertThat(result).isEqualTo(word)
        verify(wordDao).getWordById(wordId)
    }

    @Test
    fun `test updateLearningProgress updates word progress correctly`() = runTest {
        // Given
        val wordId = 1L
        val progress = 0.8f
        
        // When
        repository.updateLearningProgress(wordId, progress)
        
        // Then
        verify(wordDao).updateWordProgress(wordId, progress)
    }

    @Test
    fun `test getWordsForReview returns words due for review`() = runTest {
        // Given
        val dueWords = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4",
                lastReviewDate = System.currentTimeMillis() - 24 * 60 * 60 * 1000, // 1 day ago
                nextReviewDate = System.currentTimeMillis() - 1000 // Due
            )
        )
        whenever(wordDao.getWordsForReview(any())).thenReturn(flowOf(dueWords))

        // When
        val result = repository.getWordsForReview().first()

        // Then
        assertThat(result).isEqualTo(dueWords)
    }

    @Test
    fun `test generateReviewPlan creates review schedule`() = runTest {
        // Given
        val wordId = 1L
        val currentTime = System.currentTimeMillis()
        val reviewIntervals = listOf(1, 3, 7, 14, 30) // 间隔天数

        // When
        repository.generateReviewPlan(wordId, reviewIntervals)

        // Then
        verify(wordDao).updateNextReviewDate(eq(wordId), any())
    }

    @Test
    fun `test markWordAsReviewed updates review dates`() = runTest {
        // Given
        val wordId = 1L
        val reviewResult = 0.9f // 复习结果评分

        // When
        repository.markWordAsReviewed(wordId, reviewResult)

        // Then
        verify(wordDao).updateLastReviewDate(eq(wordId), any())
        verify(wordDao).updateWordProgress(wordId, reviewResult)
    }

    @Test
    fun `test getWordsByProgress returns words with specified progress range`() = runTest {
        // Given
        val minProgress = 0.7f
        val maxProgress = 0.9f
        val words = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4",
                progress = 0.8f
            )
        )
        whenever(wordDao.getWordsByProgress(minProgress, maxProgress)).thenReturn(flowOf(words))

        // When
        val result = repository.getWordsByProgress(minProgress, maxProgress).first()

        // Then
        assertThat(result).isEqualTo(words)
    }

    @Test
    fun `test getDailyLearningStats returns correct statistics`() = runTest {
        // Given
        val date = System.currentTimeMillis()
        val stats = mapOf(
            "wordsLearned" to 10,
            "averageProgress" to 0.75f,
            "reviewCount" to 15
        )
        whenever(wordDao.getDailyLearningStats(date)).thenReturn(flowOf(stats))

        // When
        val result = repository.getDailyLearningStats(date).first()

        // Then
        assertThat(result).isEqualTo(stats)
    }

    @Test
    fun `test resetWordProgress resets progress and review dates`() = runTest {
        // Given
        val wordId = 1L

        // When
        repository.resetWordProgress(wordId)

        // Then
        verify(wordDao).updateWordProgress(wordId, 0f)
        verify(wordDao).updateLastReviewDate(eq(wordId), null)
        verify(wordDao).updateNextReviewDate(eq(wordId), null)
    }
}
