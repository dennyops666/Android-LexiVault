package com.example.lexivault.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lexivault.data.entity.Vocabulary
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabularies ORDER BY name ASC")
    fun getAllVocabularies(): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabularies WHERE id = :id")
    fun getVocabularyById(id: Long): Flow<Vocabulary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocabulary: Vocabulary): Long

    @Update
    suspend fun updateVocabulary(vocabulary: Vocabulary)

    @Delete
    suspend fun deleteVocabulary(vocabulary: Vocabulary)

    @Query("SELECT * FROM vocabularies WHERE name LIKE :query OR description LIKE :query ORDER BY name ASC")
    fun searchVocabularies(query: String): Flow<List<Vocabulary>>
}
