package com.example.lexivault.ui.screens.mistakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.TestRecordEntity
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.TestRepository
import com.example.lexivault.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MistakesViewModel @Inject constructor(
    private val testRepository: TestRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MistakesUiState())
    val uiState: StateFlow<MistakesUiState> = _uiState.asStateFlow()

    init {
        loadMistakes()
    }

    private fun loadMistakes() {
        viewModelScope.launch {
            testRepository.getIncorrectWords()
                .combine(wordRepository.getAllWords()) { records, words ->
                    val wordMap = words.associateBy { it.id }
                    val mistakes = records.mapNotNull { record ->
                        wordMap[record.wordId]?.let { word ->
                            MistakeItem(
                                word = word,
                                record = record
                            )
                        }
                    }
                    mistakes.groupBy { it.word.id }
                        .map { (_, records) -> records.maxByOrNull { it.record.timestamp }!! }
                        .sortedByDescending { it.record.timestamp }
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "加载错题失败"
                    )
                }
                .collect { mistakes ->
                    _uiState.value = _uiState.value.copy(
                        mistakes = mistakes,
                        isLoading = false
                    )
                }
        }
    }

    fun retryWord(wordId: Long) {
        viewModelScope.launch {
            val word = wordRepository.getWordById(wordId)
            if (word != null) {
                _uiState.value = _uiState.value.copy(
                    selectedWord = word
                )
            }
        }
    }

    fun hideRetryDialog() {
        _uiState.value = _uiState.value.copy(
            selectedWord = null
        )
    }

    fun submitRetryAnswer(answer: String) {
        val word = _uiState.value.selectedWord ?: return
        viewModelScope.launch {
            val isCorrect = answer.trim().equals(word.word, ignoreCase = true)
            testRepository.recordTestResult(
                wordId = word.id,
                testType = com.example.lexivault.data.database.entity.TestType.FILL_IN_BLANK,
                isCorrect = isCorrect,
                userAnswer = answer
            )
            _uiState.value = _uiState.value.copy(
                retryResult = if (isCorrect) "回答正确！" else "回答错误",
                showRetryResult = true
            )
        }
    }

    fun hideRetryResult() {
        _uiState.value = _uiState.value.copy(
            showRetryResult = false,
            selectedWord = null,
            retryResult = null
        )
        loadMistakes()
    }
}

data class MistakesUiState(
    val mistakes: List<MistakeItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedWord: WordEntity? = null,
    val retryResult: String? = null,
    val showRetryResult: Boolean = false
)

data class MistakeItem(
    val word: WordEntity,
    val record: TestRecordEntity
)
