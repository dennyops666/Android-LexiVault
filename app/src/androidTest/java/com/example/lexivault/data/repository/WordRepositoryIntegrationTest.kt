package com.example.lexivault.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.data.database.LexiVaultDatabase
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.WordEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class WordRepositoryIntegrationTest {
    private lateinit var database: LexiVaultDatabase
    private lateinit var wordDao: WordDao
    private lateinit var repository: WordRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            LexiVaultDatabase::class.java
        ).build()
        wordDao = database.wordDao()
        repository = WordRepository(wordDao)
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveWord() = runTest {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4",
            createdAt = Instant.now().toEpochMilli()
        )

        // When
        repository.insertWord(word.toDomainModel())
        val result = repository.getAllWords().first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].word).isEqualTo("test")
        assertThat(result[0].meaning).isEqualTo("测试")
    }

    @Test
    fun testUpdateWord() = runTest {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4",
            createdAt = Instant.now().toEpochMilli()
        )
        repository.insertWord(word.toDomainModel())

        // When
        val updatedWord = word.copy(meaning = "新测试")
        repository.updateWord(updatedWord.toDomainModel())
        val result = repository.getWordById(1)

        // Then
        assertThat(result?.meaning).isEqualTo("新测试")
    }

    @Test
    fun testDeleteWord() = runTest {
        // Given
        val word = WordEntity(
            id = 1,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4",
            createdAt = Instant.now().toEpochMilli()
        )
        repository.insertWord(word.toDomainModel())

        // When
        repository.deleteWord(word.toDomainModel())
        val result = repository.getAllWords().first()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun testSearchWords() = runTest {
        // Given
        val words = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            ),
            WordEntity(
                id = 2,
                word = "testing",
                meaning = "测试中",
                category = "verb",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            ),
            WordEntity(
                id = 3,
                word = "example",
                meaning = "例子",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            )
        )
        words.forEach { repository.insertWord(it.toDomainModel()) }

        // When
        val searchResult = repository.searchWords("test").first()

        // Then
        assertThat(searchResult).hasSize(2)
        assertThat(searchResult.map { it.word }).containsExactly("test", "testing")
    }

    @Test
    fun testGetWordsByType() = runTest {
        // Given
        val words = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            ),
            WordEntity(
                id = 2,
                word = "advanced",
                meaning = "高级的",
                category = "adj",
                type = "CET6",
                createdAt = Instant.now().toEpochMilli()
            )
        )
        words.forEach { repository.insertWord(it.toDomainModel()) }

        // When
        val cet4Words = repository.getWordsByType("CET4").first()
        val cet6Words = repository.getWordsByType("CET6").first()

        // Then
        assertThat(cet4Words).hasSize(1)
        assertThat(cet6Words).hasSize(1)
        assertThat(cet4Words[0].word).isEqualTo("test")
        assertThat(cet6Words[0].word).isEqualTo("advanced")
    }

    @Test
    fun testConcurrentOperations() = runTest {
        // Given
        val words = (1..100).map { i ->
            WordEntity(
                id = i.toLong(),
                word = "word$i",
                meaning = "含义$i",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            )
        }

        // When - 并发插入
        words.forEach { word ->
            repository.insertWord(word.toDomainModel())
        }

        // Then
        val result = repository.getAllWords().first()
        assertThat(result).hasSize(100)
        assertThat(result.map { it.id }).containsExactlyElementsIn(1..100)
    }

    @Test
    fun testTransactionRollback() = runTest {
        try {
            database.runInTransaction {
                // Given
                val word = WordEntity(
                    id = 1,
                    word = "test",
                    meaning = "测试",
                    category = "noun",
                    type = "CET4",
                    createdAt = Instant.now().toEpochMilli()
                )
                repository.insertWord(word.toDomainModel())

                // When - 抛出异常触发回滚
                throw RuntimeException("Test transaction rollback")
            }
        } catch (e: RuntimeException) {
            // Then - 验证数据已回滚
            val result = repository.getAllWords().first()
            assertThat(result).isEmpty()
        }
    }
}
