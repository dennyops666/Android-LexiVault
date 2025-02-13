package com.example.lexivault.data.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.lexivault.data.database.AppDatabase
import com.example.lexivault.data.entity.Word
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
class WordDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var wordDao: WordDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        wordDao = db.wordDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetWord() = runTest {
        // Given
        val word = Word(
            word = "test",
            meaning = "测试",
            vocabularyId = 1,
            createdAt = LocalDateTime.now(),
            lastReviewedAt = null,
            nextReviewAt = null,
            reviewCount = 0,
            masteryLevel = 0
        )

        // When
        wordDao.insert(word)
        val result = wordDao.getWordById(word.id).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.word).isEqualTo("test")
        assertThat(result?.meaning).isEqualTo("测试")
    }

    @Test
    fun updateWord() = runTest {
        // Given
        val word = Word(
            word = "test",
            meaning = "测试",
            vocabularyId = 1,
            createdAt = LocalDateTime.now(),
            lastReviewedAt = null,
            nextReviewAt = null,
            reviewCount = 0,
            masteryLevel = 0
        )
        wordDao.insert(word)

        // When
        val updatedWord = word.copy(meaning = "新的测试")
        wordDao.update(updatedWord)
        val result = wordDao.getWordById(word.id).first()

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.meaning).isEqualTo("新的测试")
    }

    @Test
    fun deleteWord() = runTest {
        // Given
        val word = Word(
            word = "test",
            meaning = "测试",
            vocabularyId = 1,
            createdAt = LocalDateTime.now(),
            lastReviewedAt = null,
            nextReviewAt = null,
            reviewCount = 0,
            masteryLevel = 0
        )
        wordDao.insert(word)

        // When
        wordDao.delete(word)
        val result = wordDao.getWordById(word.id).first()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun getWordsByVocabularyId() = runTest {
        // Given
        val vocabularyId = 1L
        val words = listOf(
            Word(
                word = "test1",
                meaning = "测试1",
                vocabularyId = vocabularyId,
                createdAt = LocalDateTime.now(),
                lastReviewedAt = null,
                nextReviewAt = null,
                reviewCount = 0,
                masteryLevel = 0
            ),
            Word(
                word = "test2",
                meaning = "测试2",
                vocabularyId = vocabularyId,
                createdAt = LocalDateTime.now(),
                lastReviewedAt = null,
                nextReviewAt = null,
                reviewCount = 0,
                masteryLevel = 0
            )
        )
        words.forEach { wordDao.insert(it) }

        // When
        val result = wordDao.getWordsByVocabularyId(vocabularyId).first()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.word }).containsExactly("test1", "test2")
    }

    @Test
    fun getWordsForReview() = runTest {
        // Given
        val now = LocalDateTime.now()
        val words = listOf(
            Word(
                word = "test1",
                meaning = "测试1",
                vocabularyId = 1,
                createdAt = now.minusDays(1),
                lastReviewedAt = now.minusDays(1),
                nextReviewAt = now.minusHours(1),
                reviewCount = 1,
                masteryLevel = 1
            ),
            Word(
                word = "test2",
                meaning = "测试2",
                vocabularyId = 1,
                createdAt = now.minusDays(1),
                lastReviewedAt = now.minusDays(1),
                nextReviewAt = now.plusDays(1),
                reviewCount = 1,
                masteryLevel = 1
            )
        )
        words.forEach { wordDao.insert(it) }

        // When
        val result = wordDao.getWordsForReview(now).first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].word).isEqualTo("test1")
    }
}
