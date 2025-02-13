package com.example.lexivault.data.repository

import com.example.lexivault.data.database.dao.TestRecordDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.TestRecordEntity
import com.example.lexivault.data.database.entity.TestType
import com.example.lexivault.data.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.random.Random

class TestRepository @Inject constructor(
    private val testRecordDao: TestRecordDao,
    private val wordDao: WordDao
) {
    // 获取测试统计数据
    fun getTestStats(): Flow<Pair<Int, Int>> {
        return kotlinx.coroutines.flow.combine(
            testRecordDao.getCorrectAnswersCount(),
            testRecordDao.getTotalAnswersCount()
        ) { correct, total -> Pair(correct, total) }
    }

    // 获取错题列表
    fun getIncorrectWords(): Flow<List<TestRecordEntity>> {
        return testRecordDao.getIncorrectRecords()
    }

    // 记录测试结果
    suspend fun recordTestResult(
        wordId: Long,
        testType: TestType,
        isCorrect: Boolean,
        userAnswer: String
    ) {
        val record = TestRecordEntity(
            wordId = wordId,
            testType = testType,
            isCorrect = isCorrect,
            userAnswer = userAnswer
        )
        testRecordDao.insert(record)
    }

    // 生成选择题
    suspend fun generateMultipleChoiceQuestion(): MultipleChoiceQuestion {
        val allWords = wordDao.getAllWordsSync()
        if (allWords.isEmpty()) {
            throw IllegalStateException("No words available for testing")
        }

        val targetWord = allWords.random()
        val options = mutableListOf(targetWord.meaning)

        // 生成3个错误选项
        val remainingWords = allWords.filter { it.id != targetWord.id }.shuffled()
        options.addAll(remainingWords.take(3).map { it.meaning })

        // 随机打乱选项顺序
        options.shuffle()

        return MultipleChoiceQuestion(
            word = targetWord,
            options = options,
            correctAnswerIndex = options.indexOf(targetWord.meaning)
        )
    }

    // 生成填空题
    suspend fun generateFillInBlankQuestion(): FillInBlankQuestion {
        val allWords = wordDao.getAllWordsSync()
        if (allWords.isEmpty()) {
            throw IllegalStateException("No words available for testing")
        }

        val targetWord = allWords.random()
        return FillInBlankQuestion(
            meaning = targetWord.meaning,
            correctAnswer = targetWord.word
        )
    }
}

data class MultipleChoiceQuestion(
    val word: WordEntity,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class FillInBlankQuestion(
    val meaning: String,
    val correctAnswer: String
)
