package com.example.lexivault.ui.screens.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.TestType
import com.example.lexivault.data.repository.TestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestScreenViewModel @Inject constructor(
    private val testRepository: TestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestScreenState())
    val uiState: StateFlow<TestScreenState> = _uiState.asStateFlow()

    init {
        loadNextQuestion()
        loadTestStats()
    }

    fun loadNextQuestion() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                when (_uiState.value.currentTestType) {
                    TestType.MULTIPLE_CHOICE -> {
                        val question = testRepository.generateMultipleChoiceQuestion()
                        _uiState.value = _uiState.value.copy(
                            currentMultipleChoiceQuestion = question,
                            currentFillInBlankQuestion = null,
                            isLoading = false,
                            error = null
                        )
                    }
                    TestType.FILL_IN_BLANK -> {
                        val question = testRepository.generateFillInBlankQuestion()
                        _uiState.value = _uiState.value.copy(
                            currentMultipleChoiceQuestion = null,
                            currentFillInBlankQuestion = question,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun switchTestType(testType: TestType) {
        _uiState.value = _uiState.value.copy(currentTestType = testType)
        loadNextQuestion()
    }

    fun submitAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isCorrect = when (currentState.currentTestType) {
                TestType.MULTIPLE_CHOICE -> {
                    val question = currentState.currentMultipleChoiceQuestion
                    question?.options?.get(answer.toInt()) == question?.word?.meaning
                }
                TestType.FILL_IN_BLANK -> {
                    val question = currentState.currentFillInBlankQuestion
                    answer.trim().equals(question?.correctAnswer, ignoreCase = true)
                }
            }

            val wordId = when (currentState.currentTestType) {
                TestType.MULTIPLE_CHOICE -> currentState.currentMultipleChoiceQuestion?.word?.id
                TestType.FILL_IN_BLANK -> null
            }

            if (wordId != null) {
                testRepository.recordTestResult(
                    wordId = wordId,
                    testType = currentState.currentTestType,
                    isCorrect = isCorrect,
                    userAnswer = answer
                )
            }

            _uiState.value = currentState.copy(
                lastAnswerResult = if (isCorrect) "回答正确！" else "回答错误",
                showResult = true
            )
        }
    }

    private fun loadTestStats() {
        viewModelScope.launch {
            testRepository.getTestStats().collect { (correct, total) ->
                _uiState.value = _uiState.value.copy(
                    correctAnswers = correct,
                    totalAnswers = total
                )
            }
        }
    }

    fun hideResult() {
        _uiState.value = _uiState.value.copy(showResult = false)
        loadNextQuestion()
    }
}

data class TestScreenState(
    val currentTestType: TestType = TestType.MULTIPLE_CHOICE,
    val currentMultipleChoiceQuestion: MultipleChoiceQuestion? = null,
    val currentFillInBlankQuestion: FillInBlankQuestion? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastAnswerResult: String? = null,
    val showResult: Boolean = false,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0
)
