package com.example.lexivault.data.database.dao

import androidx.room.*
import com.example.lexivault.data.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word: WordEntity)

    @Insert
    suspend fun insertAll(words: List<WordEntity>)

    @Update
    suspend fun update(word: WordEntity)

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): WordEntity?

    @Query("SELECT * FROM words ORDER BY id DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE type = :type")
    fun getWordsByType(type: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE category = :category")
    fun getWordsByCategory(category: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE word LIKE :query OR meaning LIKE :query")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isBookmarked = 1")
    fun getBookmarkedWords(): Flow<List<WordEntity>>

    @Query("UPDATE words SET isBookmarked = :isBookmarked WHERE id = :wordId")
    suspend fun updateBookmarkStatus(wordId: Long, isBookmarked: Boolean)

    @Query("""
        SELECT 
            COUNT(*) as totalWords,
            SUM(CASE WHEN reviewCount > 0 THEN 1 ELSE 0 END) as reviewedWords,
            SUM(CASE WHEN isBookmarked = 1 THEN 1 ELSE 0 END) as bookmarkedWords,
            SUM(reviewCount) as totalReviews
        FROM words
    """)
    fun getStudyStats(): Flow<Map<String, Int>>

    @Query("SELECT COUNT(*) FROM words WHERE lastReviewTime >= :startTime")
    fun getDailyWordCount(startTime: Long): Flow<Int>
}
