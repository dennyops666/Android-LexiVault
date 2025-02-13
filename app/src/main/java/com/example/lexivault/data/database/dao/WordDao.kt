package com.example.lexivault.data.database.dao

import androidx.room.*
import com.example.lexivault.data.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    suspend fun getAllWordsSync(): List<WordEntity>

    @Query("SELECT * FROM words WHERE vocabularyId = :vocabularyId ORDER BY createdAt DESC")
    fun getWordsByVocabularyId(vocabularyId: Long): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isBookmarked = 1")
    fun getBookmarkedWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE learned = :type")
    fun getWordsByType(type: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE vocabularyId IN (SELECT id FROM vocabularies WHERE category = :category)")
    fun getWordsByCategory(category: String): Flow<List<WordEntity>>

    @Query("UPDATE words SET isBookmarked = :isBookmarked WHERE id = :wordId")
    suspend fun updateBookmarkStatus(wordId: Long, isBookmarked: Boolean)

    @Query("UPDATE words SET learned = :learned, lastReviewTime = :lastReviewTime WHERE id = :wordId")
    suspend fun updateLearningStatus(wordId: Long, learned: Int, lastReviewTime: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM words WHERE learned > 0 AND lastReviewTime >= :since")
    suspend fun getLearnedCountSince(since: Long): Int

    @Query("SELECT COUNT(*) as count, learned as status FROM words GROUP BY learned")
    fun getStudyStats(): Flow<List<StudyStats>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Update
    suspend fun update(word: WordEntity)

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("DELETE FROM words WHERE vocabularyId = :vocabularyId")
    suspend fun deleteByVocabularyId(vocabularyId: Long)

    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM words WHERE vocabularyId = :vocabularyId")
    suspend fun getCountByVocabularyId(vocabularyId: Long): Int
}

data class StudyStats(
    val count: Int,
    val status: Int
) 