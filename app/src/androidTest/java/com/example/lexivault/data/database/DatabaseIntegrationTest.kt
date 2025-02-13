package com.example.lexivault.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.data.dao.ReminderSettingsDao
import com.example.lexivault.data.dao.VocabularyDao
import com.example.lexivault.data.dao.WordDao
import com.example.lexivault.data.entity.ReminderSettings
import com.example.lexivault.data.entity.Vocabulary
import com.example.lexivault.data.entity.Word
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {
    private lateinit var db: AppDatabase
    private lateinit var wordDao: WordDao
    private lateinit var vocabularyDao: VocabularyDao
    private lateinit var reminderSettingsDao: ReminderSettingsDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        wordDao = db.wordDao()
        vocabularyDao = db.vocabularyDao()
        reminderSettingsDao = db.reminderSettingsDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testVocabularyWithWords() = runTest {
        // Given
        val vocabulary = Vocabulary(
            id = 1,
            name = "CET4",
            description = "大学英语四级词汇"
        )
        vocabularyDao.insertVocabulary(vocabulary)

        val words = listOf(
            Word(
                id = 1,
                word = "test",
                meaning = "测试",
                vocabularyId = vocabulary.id,
                category = "noun"
            ),
            Word(
                id = 2,
                word = "example",
                meaning = "例子",
                vocabularyId = vocabulary.id,
                category = "noun"
            )
        )
        words.forEach { wordDao.insertWord(it) }

        // When
        val vocabularyWithWords = vocabularyDao.getVocabularyWithWords(vocabulary.id).first()

        // Then
        assertThat(vocabularyWithWords.vocabulary).isEqualTo(vocabulary)
        assertThat(vocabularyWithWords.words).hasSize(2)
        assertThat(vocabularyWithWords.words).containsExactlyElementsIn(words)
    }

    @Test
    fun testWordLearningProgress() = runTest {
        // Given
        val word = Word(
            id = 1,
            word = "test",
            meaning = "测试",
            vocabularyId = 1,
            category = "noun"
        )
        wordDao.insertWord(word)

        // When
        wordDao.updateWordProgress(word.id, 0.8f)
        val updatedWord = wordDao.getWordById(word.id).first()

        // Then
        assertThat(updatedWord.progress).isEqualTo(0.8f)
    }

    @Test
    fun testReminderSettingsWithWords() = runTest {
        // Given
        val settings = ReminderSettings(
            userId = 1,
            enabled = true,
            reminderTime = LocalTime.of(20, 0),
            daysOfWeek = listOf(1, 2, 3, 4, 5)
        )
        reminderSettingsDao.insertReminderSettings(settings)

        val words = listOf(
            Word(
                id = 1,
                word = "test",
                meaning = "测试",
                vocabularyId = 1,
                nextReviewDate = System.currentTimeMillis()
            ),
            Word(
                id = 2,
                word = "example",
                meaning = "例子",
                vocabularyId = 1,
                nextReviewDate = System.currentTimeMillis()
            )
        )
        words.forEach { wordDao.insertWord(it) }

        // When
        val wordsForReview = wordDao.getWordsForReview(System.currentTimeMillis()).first()

        // Then
        assertThat(wordsForReview).hasSize(2)
    }

    @Test
    fun testTransactionRollback() = runTest {
        // Given
        val vocabulary = Vocabulary(
            id = 1,
            name = "CET4",
            description = "大学英语四级词汇"
        )

        try {
            db.runInTransaction {
                vocabularyDao.insertVocabulary(vocabulary)
                // 模拟错误
                throw IllegalStateException("Transaction test")
            }
        } catch (e: IllegalStateException) {
            // Expected exception
        }

        // When
        val result = vocabularyDao.getVocabularyById(vocabulary.id).first()

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun testCascadeDelete() = runTest {
        // Given
        val vocabulary = Vocabulary(
            id = 1,
            name = "CET4",
            description = "大学英语四级词汇"
        )
        vocabularyDao.insertVocabulary(vocabulary)

        val word = Word(
            id = 1,
            word = "test",
            meaning = "测试",
            vocabularyId = vocabulary.id,
            category = "noun"
        )
        wordDao.insertWord(word)

        // When
        vocabularyDao.deleteVocabulary(vocabulary)
        val words = wordDao.getAllWords().first()

        // Then
        assertThat(words).isEmpty()
    }

    @Test
    fun testWordSearchAndFilter() = runTest {
        // Given
        val words = listOf(
            Word(
                id = 1,
                word = "test",
                meaning = "测试",
                vocabularyId = 1,
                category = "noun"
            ),
            Word(
                id = 2,
                word = "testing",
                meaning = "测试中",
                vocabularyId = 1,
                category = "verb"
            ),
            Word(
                id = 3,
                word = "example",
                meaning = "例子",
                vocabularyId = 1,
                category = "noun"
            )
        )
        words.forEach { wordDao.insertWord(it) }

        // When
        val searchResults = wordDao.searchWords("test").first()
        val filteredResults = wordDao.getWordsByCategory("noun").first()

        // Then
        assertThat(searchResults).hasSize(2)
        assertThat(filteredResults).hasSize(2)
    }
}
