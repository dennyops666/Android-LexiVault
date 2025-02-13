package com.example.lexivault.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lexivault.data.entity.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE vocabulary_id = :vocabularyId ORDER BY word ASC")
    fun getWordsByVocabularyId(vocabularyId: Long): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordById(id: Long): Flow<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("DELETE FROM words WHERE vocabulary_id = :vocabularyId")
    suspend fun deleteWordsByVocabularyId(vocabularyId: Long)

    @Query("SELECT * FROM words WHERE vocabulary_id = :vocabularyId AND (word LIKE :query OR meaning LIKE :query OR example LIKE :query) ORDER BY word ASC")
    fun searchWords(vocabularyId: Long, query: String): Flow<List<Word>>
}
