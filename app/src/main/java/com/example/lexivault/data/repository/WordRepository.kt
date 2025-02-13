package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    // 基本的CRUD操作
    suspend fun insertWord(word: WordEntity) = wordDao.insert(word)
    suspend fun updateWord(word: WordEntity) = wordDao.update(word)
    suspend fun deleteWord(word: WordEntity) = wordDao.delete(word)
    suspend fun getWordById(id: Long) = wordDao.getWordById(id)

    // 获取所有单词
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    // 按类型获取单词（如四级、六级等）
    fun getWordsByType(type: String): Flow<List<WordEntity>> = wordDao.getWordsByType(type)

    // 按分类获取单词（如动词、名词等）
    fun getWordsByCategory(category: String): Flow<List<WordEntity>> = wordDao.getWordsByCategory(category)

    // 搜索单词
    fun searchWords(query: String): Flow<List<WordEntity>> = wordDao.searchWords("%$query%")

    // 获取生词本中的单词
    fun getBookmarkedWords(): Flow<List<WordEntity>> = wordDao.getBookmarkedWords()

    // 更新单词的生词本状态
    suspend fun updateBookmarkStatus(wordId: Long, isBookmarked: Boolean) {
        wordDao.updateBookmarkStatus(wordId, isBookmarked)
    }

    // 更新复习次数和时间
    suspend fun updateReviewInfo(wordId: Long) {
        val word = wordDao.getWordById(wordId)
        word?.let {
            wordDao.update(it.copy(
                reviewCount = it.reviewCount + 1,
                lastReviewTime = System.currentTimeMillis()
            ))
        }
    }

    // 批量插入单词（用于导入词库）
    suspend fun insertWords(words: List<WordEntity>) = wordDao.insertAll(words)

    // 获取学习统计信息
    fun getStudyStats(): Flow<Map<String, Int>> = wordDao.getStudyStats()

    // 获取今日学习单词数量
    suspend fun getTodayLearnedCount(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis
        return wordDao.getLearnedCountSince(todayStart)
    }
}
