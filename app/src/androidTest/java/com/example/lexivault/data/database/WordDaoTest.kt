package com.example.lexivault.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.WordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class WordDaoTest {
    private lateinit var database: LexiVaultDatabase
    private lateinit var wordDao: WordDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            LexiVaultDatabase::class.java
        ).build()
        wordDao = database.wordDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetWord() = runTest {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4"
        )

        // When
        wordDao.insert(word)
        val result = wordDao.getWordById(1)

        // Then
        assertThat(result).isEqualTo(word)
    }

    @Test
    fun searchWords() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "testing", meaning = "测试中", category = "verb", type = "CET4"),
            WordEntity(id = 3, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        words.forEach { wordDao.insert(it) }

        // When
        val results = wordDao.searchWords("%test%").first()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results.map { it.word }).containsExactly("test", "testing")
    }

    @Test
    fun getWordsByCategory() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "testing", meaning = "测试中", category = "verb", type = "CET4"),
            WordEntity(id = 3, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        words.forEach { wordDao.insert(it) }

        // When
        val results = wordDao.getWordsByCategory("noun").first()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results.map { it.word }).containsExactly("test", "example")
    }

    @Test
    fun updateBookmarkStatus() = runTest {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4",
            isBookmarked = false
        )
        wordDao.insert(word)

        // When
        wordDao.updateBookmarkStatus(1, true)
        val result = wordDao.getWordById(1)

        // Then
        assertThat(result?.isBookmarked).isTrue()
    }

    @Test
    fun getBookmarkedWords() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4", isBookmarked = true),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4", isBookmarked = false)
        )
        words.forEach { wordDao.insert(it) }

        // When
        val results = wordDao.getBookmarkedWords().first()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results.first().word).isEqualTo("test")
    }

    @Test
    fun getStudyStats() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4", 
                      reviewCount = 2, isBookmarked = true),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4", 
                      reviewCount = 0, isBookmarked = false)
        )
        words.forEach { wordDao.insert(it) }

        // When
        val stats = wordDao.getStudyStats().first()

        // Then
        assertThat(stats["totalWords"]).isEqualTo(2)
        assertThat(stats["reviewedWords"]).isEqualTo(1)
        assertThat(stats["bookmarkedWords"]).isEqualTo(1)
        assertThat(stats["totalReviews"]).isEqualTo(2)
    }
}
