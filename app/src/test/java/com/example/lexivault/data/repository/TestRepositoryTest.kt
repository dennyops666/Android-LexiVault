package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.TestRecordDao
import com.example.lexivault.data.database.entity.TestRecordEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class TestRepositoryTest {
    private lateinit var testRecordDao: TestRecordDao
    private lateinit var repository: TestRepository

    @Before
    fun setup() {
        testRecordDao = mock()
        repository = TestRepository(testRecordDao)
    }

    @Test
    fun `test saveTestRecord saves test record to dao`() = runTest {
        // Given
        val record = TestRecordEntity(
            id = 1,
            wordId = 1,
            isCorrect = true,
            testType = "MULTIPLE_CHOICE",
            timestamp = LocalDateTime.now()
        )

        // When
        repository.saveTestRecord(record)

        // Then
        verify(testRecordDao).insert(record)
    }

    @Test
    fun `test getTestRecords returns records for word`() = runTest {
        // Given
        val wordId = 1L
        val records = listOf(
            TestRecordEntity(
                id = 1,
                wordId = wordId,
                isCorrect = true,
                testType = "MULTIPLE_CHOICE",
                timestamp = LocalDateTime.now()
            ),
            TestRecordEntity(
                id = 2,
                wordId = wordId,
                isCorrect = false,
                testType = "SPELLING",
                timestamp = LocalDateTime.now()
            )
        )
        whenever(testRecordDao.getTestRecordsForWord(wordId)).thenReturn(flowOf(records))

        // When
        val result = repository.getTestRecordsForWord(wordId).first()

        // Then
        assertThat(result).isEqualTo(records)
    }

    @Test
    fun `test getTestStatistics returns correct statistics`() = runTest {
        // Given
        val stats = mapOf(
            "totalTests" to 10,
            "correctAnswers" to 7,
            "incorrectAnswers" to 3
        )
        whenever(testRecordDao.getTestStatistics()).thenReturn(flowOf(stats))

        // When
        val result = repository.getTestStatistics().first()

        // Then
        assertThat(result).containsExactlyEntriesIn(stats)
    }

    @Test
    fun `test getMistakes returns only incorrect answers`() = runTest {
        // Given
        val records = listOf(
            TestRecordEntity(
                id = 1,
                wordId = 1,
                isCorrect = false,
                testType = "MULTIPLE_CHOICE",
                timestamp = LocalDateTime.now()
            )
        )
        whenever(testRecordDao.getMistakes()).thenReturn(flowOf(records))

        // When
        val result = repository.getMistakes().first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().isCorrect).isFalse()
    }
}
