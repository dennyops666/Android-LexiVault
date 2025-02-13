package com.example.lexivault.data.database.dao

import androidx.room.*
import com.example.lexivault.data.database.entity.TestRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestRecordDao {
    @Insert
    suspend fun insert(testRecord: TestRecordEntity)

    @Query("SELECT * FROM test_records WHERE wordId = :wordId ORDER BY timestamp DESC")
    fun getTestRecordsForWord(wordId: Long): Flow<List<TestRecordEntity>>

    @Query("SELECT * FROM test_records WHERE isCorrect = 0 ORDER BY timestamp DESC")
    fun getIncorrectRecords(): Flow<List<TestRecordEntity>>

    @Query("SELECT COUNT(*) FROM test_records WHERE isCorrect = 1")
    fun getCorrectAnswersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM test_records")
    fun getTotalAnswersCount(): Flow<Int>

    @Query("DELETE FROM test_records WHERE wordId = :wordId")
    suspend fun deleteRecordsForWord(wordId: Long)
}
