package com.example.lexivault.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lexivault.data.database.AppDatabase
import com.example.lexivault.data.database.entity.TestRecordEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TestRecordDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var testRecordDao: TestRecordDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        testRecordDao = database.testRecordDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testInsertAndGetTestRecord() = runTest {
        // Given
        val record = TestRecordEntity(
            id = 1,
            wordId = 1,
            isCorrect = true,
            testType = "MULTIPLE_CHOICE",
            timestamp = LocalDateTime.now()
        )

        // When
        testRecordDao.insert(record)
        val allRecords = testRecordDao.getAllTestRecords().first()

        // Then
        assertThat(allRecords).contains(record)
    }

    @Test
    fun testGetTestRecordsForWord() = runTest {
        // Given
        val record1 = TestRecordEntity(
            id = 1,
            wordId = 1,
            isCorrect = true,
            testType = "MULTIPLE_CHOICE",
            timestamp = LocalDateTime.now()
        )
        val record2 = TestRecordEntity(
            id = 2,
            wordId = 1,
            isCorrect = false,
            testType = "SPELLING",
            timestamp = LocalDateTime.now()
        )
        testRecordDao.insert(record1)
        testRecordDao.insert(record2)

        // When
        val wordRecords = testRecordDao.getTestRecordsForWord(1).first()

        // Then
        assertThat(wordRecords).hasSize(2)
        assertThat(wordRecords).containsAtLeast(record1, record2)
    }

    @Test
    fun testGetTestStatistics() = runTest {
        // Given
        val record1 = TestRecordEntity(
            id = 1,
            wordId = 1,
            isCorrect = true,
            testType = "MULTIPLE_CHOICE",
            timestamp = LocalDateTime.now()
        )
        val record2 = TestRecordEntity(
            id = 2,
            wordId = 2,
            isCorrect = false,
            testType = "SPELLING",
            timestamp = LocalDateTime.now()
        )
        testRecordDao.insert(record1)
        testRecordDao.insert(record2)

        // When
        val stats = testRecordDao.getTestStatistics().first()

        // Then
        assertThat(stats["totalTests"]).isEqualTo(2)
        assertThat(stats["correctAnswers"]).isEqualTo(1)
        assertThat(stats["incorrectAnswers"]).isEqualTo(1)
    }

    @Test
    fun testGetMistakes() = runTest {
        // Given
        val correctRecord = TestRecordEntity(
            id = 1,
            wordId = 1,
            isCorrect = true,
            testType = "MULTIPLE_CHOICE",
            timestamp = LocalDateTime.now()
        )
        val incorrectRecord = TestRecordEntity(
            id = 2,
            wordId = 2,
            isCorrect = false,
            testType = "SPELLING",
            timestamp = LocalDateTime.now()
        )
        testRecordDao.insert(correctRecord)
        testRecordDao.insert(incorrectRecord)

        // When
        val mistakes = testRecordDao.getMistakes().first()

        // Then
        assertThat(mistakes).hasSize(1)
        assertThat(mistakes.first().isCorrect).isFalse()
    }

    @Test
    fun testDeleteTestRecord() = runTest {
        // Given
        val record = TestRecordEntity(
            id = 1,
            wordId = 1,
            isCorrect = true,
            testType = "MULTIPLE_CHOICE",
            timestamp = LocalDateTime.now()
        )
        testRecordDao.insert(record)

        // When
        testRecordDao.delete(record)
        val allRecords = testRecordDao.getAllTestRecords().first()

        // Then
        assertThat(allRecords).isEmpty()
    }
}
