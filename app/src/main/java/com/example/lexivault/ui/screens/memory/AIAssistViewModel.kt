package com.example.lexivault.ui.screens.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.OpenAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIAssistViewModel @Inject constructor(
    private val openAIRepository: OpenAIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistUiState())
    val uiState: StateFlow<AIAssistUiState> = _uiState.asStateFlow()

    fun generateMemoryTechnique(word: WordEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val technique = openAIRepository.generateMemoryTechnique(
                    word.word,
                    word.meaning
                )
                _uiState.value = _uiState.value.copy(
                    memoryTechnique = technique,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "生成记忆方法失败：${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun generateExampleSentences(word: WordEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val sentences = openAIRepository.generateExampleSentences(
                    word.word,
                    word.meaning
                )
                _uiState.value = _uiState.value.copy(
                    exampleSentences = sentences,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "生成例句失败：${e.message}",
                    isLoading = false
                )
            }
        }
    }
}

data class AIAssistUiState(
    val memoryTechnique: String? = null,
    val exampleSentences: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
