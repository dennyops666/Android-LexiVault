package com.example.lexivault.ui.screens.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryUiState())
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    private var currentWordList = listOf<WordEntity>()
    private var currentWordIndex = 0

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            repository.getAllWords().collect { words ->
                currentWordList = words
                if (words.isNotEmpty()) {
                    updateCurrentWord(words[currentWordIndex])
                }
            }
        }
    }

    private fun updateCurrentWord(word: WordEntity) {
        _uiState.value = _uiState.value.copy(
            currentWord = word,
            isLoading = false
        )
    }

    fun nextWord() {
        if (currentWordList.isEmpty()) return
        currentWordIndex = (currentWordIndex + 1) % currentWordList.size
        updateCurrentWord(currentWordList[currentWordIndex])
    }

    fun previousWord() {
        if (currentWordList.isEmpty()) return
        currentWordIndex = if (currentWordIndex > 0) currentWordIndex - 1 else currentWordList.size - 1
        updateCurrentWord(currentWordList[currentWordIndex])
    }

    fun markAsKnown() {
        // TODO: 标记单词为已掌握
    }

    fun addToWordBook() {
        // TODO: 添加到生词本
    }
}

data class MemoryUiState(
    val currentWord: WordEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
