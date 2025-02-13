package com.example.lexivault.ui.screens.library

import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.WordRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WordLibraryViewModelTest {
    private lateinit var wordRepository: WordRepository
    private lateinit var viewModel: WordLibraryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wordRepository = mock()
        viewModel = WordLibraryViewModel(wordRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadWords loads all words from repository`() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        whenever(wordRepository.getAllWords()).thenReturn(flowOf(words))

        // When
        viewModel.loadWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.words).isEqualTo(words)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `test searchWords filters words correctly`() = runTest {
        // Given
        val query = "test"
        val matchingWords = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "testing", meaning = "测试中", category = "verb", type = "CET4")
        )
        whenever(wordRepository.searchWords(query)).thenReturn(flowOf(matchingWords))

        // When
        viewModel.searchWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.searchResults).isEqualTo(matchingWords)
    }

    @Test
    fun `test filterByCategory filters words by category`() = runTest {
        // Given
        val category = "noun"
        val nouns = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        whenever(wordRepository.getWordsByCategory(category)).thenReturn(flowOf(nouns))

        // When
        viewModel.filterByCategory(category)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.filteredWords).isEqualTo(nouns)
    }

    @Test
    fun `test addWord adds new word to repository`() = runTest {
        // Given
        val newWord = WordEntity(
            id = 0,
            word = "test",
            meaning = "测试",
            category = "noun",
            type = "CET4"
        )

        // When
        viewModel.addWord(newWord)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(wordRepository).insertWord(newWord)
    }

    @Test
    fun `test deleteWord removes word from repository`() = runTest {
        // Given
        val wordId = 1L

        // When
        viewModel.deleteWord(wordId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(wordRepository).deleteWord(wordId)
    }

    @Test
    fun `test initial state is correct`() {
        // When initial state
        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.words).isEmpty()
        assertThat(initialState.searchResults).isEmpty()
        assertThat(initialState.filteredWords).isEmpty()
        assertThat(initialState.error).isNull()
    }
}
