package com.example.lexivault.data.database.dao

import androidx.room.*
import com.example.lexivault.data.database.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabularies ORDER BY createdAt DESC")
    fun getAllVocabularies(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabularies WHERE id = :id")
    suspend fun getVocabularyById(id: Long): VocabularyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vocabulary: VocabularyEntity): Long

    @Update
    suspend fun update(vocabulary: VocabularyEntity)

    @Delete
    suspend fun delete(vocabulary: VocabularyEntity)

    @Query("DELETE FROM vocabularies")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM vocabularies")
    suspend fun getCount(): Int

    @Query("SELECT * FROM vocabularies WHERE name LIKE :query OR category LIKE :query ORDER BY createdAt DESC")
    fun searchVocabularies(query: String): Flow<List<VocabularyEntity>>
} 