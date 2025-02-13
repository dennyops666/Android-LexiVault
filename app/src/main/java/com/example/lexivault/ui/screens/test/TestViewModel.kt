package com.example.lexivault.ui.screens.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class TestType {
    MULTIPLE_CHOICE,
    SPELLING
}

data class TestItem(
    val word: String,
    val meaning: String,
    val type: TestType,
    val options: List<String> = emptyList()
)

data class TestFeedback(
    val isCorrect: Boolean,
    val correctAnswer: String
)

@HiltViewModel
class TestViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _currentTest = MutableStateFlow<TestItem?>(null)
    val currentTest: StateFlow<TestItem?> = _currentTest

    var progress by mutableStateOf(0f)
        private set

    var feedback by mutableStateOf<TestFeedback?>(null)
        private set

    var correctRate by mutableStateOf(0f)
        private set

    private var totalQuestions = 0
    private var correctAnswers = 0
    private var currentWord: WordEntity? = null
    private var testWords: List<WordEntity> = emptyList()
    private var wrongAnswers: MutableList<WordEntity> = mutableListOf()

    init {
        loadTestWords()
    }

    private fun loadTestWords() {
        viewModelScope.launch {
            testWords = wordRepository.getAllWords().value ?: emptyList()
            totalQuestions = testWords.size
            if (testWords.isNotEmpty()) {
                createNextTest()
            }
        }
    }

    private fun createNextTest() {
        currentWord = testWords.randomOrNull()
        currentWord?.let { word ->
            val isMultipleChoice = Random.nextBoolean()
            if (isMultipleChoice) {
                createMultipleChoiceTest(word)
            } else {
                createSpellingTest(word)
            }
        } ?: run {
            _currentTest.value = null
            calculateFinalScore()
        }
    }

    private fun createMultipleChoiceTest(word: WordEntity) {
        val options = mutableListOf(word.meaning)
        val otherMeanings = testWords.filter { it.id != word.id }
            .map { it.meaning }
            .shuffled()
            .take(3)
        options.addAll(otherMeanings)
        options.shuffle()

        _currentTest.value = TestItem(
            word = word.word,
            meaning = word.meaning,
            type = TestType.MULTIPLE_CHOICE,
            options = options
        )
    }

    private fun createSpellingTest(word: WordEntity) {
        _currentTest.value = TestItem(
            word = word.word,
            meaning = word.meaning,
            type = TestType.SPELLING
        )
    }

    fun submitAnswer(answer: String) {
        currentWord?.let { word ->
            val isCorrect = when (currentTest.value?.type) {
                TestType.MULTIPLE_CHOICE -> answer == word.meaning
                TestType.SPELLING -> answer.trim().equals(word.word, ignoreCase = true)
                null -> false
            }

            if (isCorrect) {
                correctAnswers++
            } else {
                wrongAnswers.add(word)
            }

            feedback = TestFeedback(
                isCorrect = isCorrect,
                correctAnswer = when (currentTest.value?.type) {
                    TestType.MULTIPLE_CHOICE -> word.meaning
                    TestType.SPELLING -> word.word
                    null -> ""
                }
            )

            progress = (correctAnswers + wrongAnswers.size).toFloat() / totalQuestions
        }
    }

    fun nextTest() {
        feedback = null
        createNextTest()
    }

    fun skipCurrentTest() {
        currentWord?.let { wrongAnswers.add(it) }
        progress = (correctAnswers + wrongAnswers.size).toFloat() / totalQuestions
        nextTest()
    }

    private fun calculateFinalScore() {
        correctRate = (correctAnswers * 100f) / totalQuestions
        // 保存错题到错题本
        viewModelScope.launch {
            wrongAnswers.forEach { word ->
                wordRepository.updateWord(word.copy(isBookmarked = true))
            }
        }
    }
}
