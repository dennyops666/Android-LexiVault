package com.example.lexivault.ui.screens.wordbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.WordBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordBookViewModel @Inject constructor(
    private val repository: WordBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordBookUiState())
    val uiState: StateFlow<WordBookUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadWords()
        observeSearchQuery()
    }

    private fun loadWords() {
        viewModelScope.launch {
            repository.getWordsInWordBook()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
                .collect { words ->
                    _uiState.value = _uiState.value.copy(
                        words = words,
                        isLoading = false
                    )
                }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isEmpty()) {
                        repository.getWordsInWordBook()
                    } else {
                        repository.searchWordsInWordBook(query)
                    }
                }
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
                .collect { words ->
                    _uiState.value = _uiState.value.copy(
                        words = words,
                        isLoading = false
                    )
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun removeFromWordBook(word: WordEntity) {
        viewModelScope.launch {
            repository.removeFromWordBook(word.id)
        }
    }
}

data class WordBookUiState(
    val words: List<WordEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
