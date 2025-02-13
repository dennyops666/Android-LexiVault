package com.example.lexivault.data.sync

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.data.database.LexiVaultDatabase
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.network.api.WordSyncApi
import com.example.lexivault.data.repository.WordRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class DataSyncTest {
    private lateinit var database: LexiVaultDatabase
    private lateinit var wordDao: WordDao
    private lateinit var repository: WordRepository
    private lateinit var syncApi: WordSyncApi
    private lateinit var dataSync: DataSync

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            LexiVaultDatabase::class.java
        ).build()
        wordDao = database.wordDao()
        repository = WordRepository(wordDao)
        syncApi = mockk()
        dataSync = DataSync(repository, syncApi)
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testSyncFromRemote() = runTest {
        // Given
        val remoteWords = listOf(
            WordEntity(
                id = 1,
                word = "remote1",
                meaning = "远程1",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            ),
            WordEntity(
                id = 2,
                word = "remote2",
                meaning = "远程2",
                category = "verb",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            )
        )

        coEvery { syncApi.getWords() } returns remoteWords.map { it.toDomainModel() }

        // When
        dataSync.syncFromRemote()
        val localWords = repository.getAllWords().first()

        // Then
        assertThat(localWords).hasSize(2)
        assertThat(localWords.map { it.word }).containsExactly("remote1", "remote2")
    }

    @Test
    fun testSyncToRemote() = runTest {
        // Given
        val localWords = listOf(
            WordEntity(
                id = 1,
                word = "local1",
                meaning = "本地1",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            ),
            WordEntity(
                id = 2,
                word = "local2",
                meaning = "本地2",
                category = "verb",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            )
        )
        localWords.forEach { repository.insertWord(it.toDomainModel()) }

        coEvery { syncApi.uploadWords(any()) } returns Unit

        // When
        dataSync.syncToRemote()

        // Then - 验证API是否被正确调用
        coEvery { syncApi.uploadWords(match { words ->
            words.size == 2 && 
            words.map { it.word }.containsAll(listOf("local1", "local2"))
        }) }
    }

    @Test
    fun testConflictResolution() = runTest {
        // Given - 本地和远程有相同ID但不同内容的单词
        val localWord = WordEntity(
            id = 1,
            word = "test",
            meaning = "本地版本",
            category = "noun",
            type = "CET4",
            createdAt = Instant.now().toEpochMilli() - 1000 // 较早的时间戳
        )
        val remoteWord = localWord.copy(
            meaning = "远程版本",
            createdAt = Instant.now().toEpochMilli() // 较新的时间戳
        )

        repository.insertWord(localWord.toDomainModel())
        coEvery { syncApi.getWords() } returns listOf(remoteWord.toDomainModel())

        // When
        dataSync.syncFromRemote()
        val result = repository.getWordById(1)

        // Then - 应该保留较新的远程版本
        assertThat(result?.meaning).isEqualTo("远程版本")
    }

    @Test
    fun testPartialSync() = runTest {
        // Given - 模拟部分同步场景
        val timestamp = Instant.now().toEpochMilli() - 3600000 // 1小时前
        val newWords = listOf(
            WordEntity(
                id = 1,
                word = "new1",
                meaning = "新词1",
                category = "noun",
                type = "CET4",
                createdAt = Instant.now().toEpochMilli()
            )
        )

        coEvery { syncApi.getWordsAfter(timestamp) } returns newWords.map { it.toDomainModel() }

        // When
        dataSync.syncFromRemoteAfter(timestamp)
        val result = repository.getAllWords().first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].word).isEqualTo("new1")
    }

    @Test
    fun testSyncError() = runTest {
        // Given - 模拟同步错误
        coEvery { syncApi.getWords() } throws Exception("Network error")

        try {
            // When
            dataSync.syncFromRemote()
        } catch (e: Exception) {
            // Then - 本地数据应保持不变
            val localWords = repository.getAllWords().first()
            assertThat(localWords).isEmpty()
        }
    }
}
