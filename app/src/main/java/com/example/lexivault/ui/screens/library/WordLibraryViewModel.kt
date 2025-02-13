package com.example.lexivault.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.LibraryType
import com.example.lexivault.data.database.entity.WordLibraryEntity
import com.example.lexivault.data.repository.WordLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordLibraryViewModel @Inject constructor(
    private val repository: WordLibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordLibraryUiState())
    val uiState: StateFlow<WordLibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibraries()
        importBuiltInLibraries()
    }

    private fun loadLibraries() {
        viewModelScope.launch {
            repository.getAllLibraries()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
                .collect { libraries ->
                    _uiState.value = _uiState.value.copy(
                        libraries = libraries,
                        isLoading = false
                    )
                }
        }
    }

    private fun importBuiltInLibraries() {
        viewModelScope.launch {
            try {
                repository.importBuiltInLibrary(
                    "cet4.json",
                    "CET-4 词库",
                    "大学英语四级词汇"
                )
                repository.importBuiltInLibrary(
                    "cet6.json",
                    "CET-6 词库",
                    "大学英语六级词汇"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "导入内置词库失败：${e.message}"
                )
            }
        }
    }

    fun createLibrary(name: String, description: String) {
        viewModelScope.launch {
            try {
                repository.createLibrary(name, description)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "创建词库失败：${e.message}"
                )
            }
        }
    }

    fun deleteLibrary(library: WordLibraryEntity) {
        viewModelScope.launch {
            try {
                repository.deleteLibrary(library)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "删除词库失败：${e.message}"
                )
            }
        }
    }
}

data class WordLibraryUiState(
    val libraries: List<WordLibraryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
