package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.VocabularyDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.VocabularyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepository @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val wordDao: WordDao
) {
    fun getAllVocabularies(): Flow<List<VocabularyEntity>> =
        vocabularyDao.getAllVocabularies()

    suspend fun getVocabularyById(id: Long): VocabularyEntity? =
        withContext(Dispatchers.IO) {
            vocabularyDao.getVocabularyById(id)
        }

    suspend fun insertVocabulary(vocabulary: VocabularyEntity): Long =
        withContext(Dispatchers.IO) {
            vocabularyDao.insert(vocabulary)
        }

    suspend fun updateVocabulary(vocabulary: VocabularyEntity) =
        withContext(Dispatchers.IO) {
            vocabularyDao.update(vocabulary)
        }

    suspend fun deleteVocabulary(vocabulary: VocabularyEntity) =
        withContext(Dispatchers.IO) {
            vocabularyDao.delete(vocabulary)
            wordDao.deleteByVocabularyId(vocabulary.id)
        }

    fun searchVocabularies(query: String): Flow<List<VocabularyEntity>> =
        vocabularyDao.searchVocabularies("%$query%")
}
