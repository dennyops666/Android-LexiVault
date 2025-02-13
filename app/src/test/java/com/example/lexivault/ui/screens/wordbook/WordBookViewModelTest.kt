package com.example.lexivault.ui.screens.wordbook

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
class WordBookViewModelTest {
    private lateinit var wordRepository: WordRepository
    private lateinit var viewModel: WordBookViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wordRepository = mock()
        viewModel = WordBookViewModel(wordRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadBookmarkedWords loads only bookmarked words`() = runTest {
        // Given
        val bookmarkedWords = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4", isBookmarked = true),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4", isBookmarked = true)
        )
        whenever(wordRepository.getBookmarkedWords()).thenReturn(flowOf(bookmarkedWords))

        // When
        viewModel.loadBookmarkedWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.bookmarkedWords).isEqualTo(bookmarkedWords)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `test removeBookmark removes word from bookmarks`() = runTest {
        // Given
        val wordId = 1L

        // When
        viewModel.removeBookmark(wordId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(wordRepository).updateBookmarkStatus(wordId, false)
    }

    @Test
    fun `test searchBookmarkedWords filters bookmarked words`() = runTest {
        // Given
        val query = "test"
        val matchingWords = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4", isBookmarked = true)
        )
        whenever(wordRepository.searchBookmarkedWords(query)).thenReturn(flowOf(matchingWords))

        // When
        viewModel.searchBookmarkedWords(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.searchResults).isEqualTo(matchingWords)
    }

    @Test
    fun `test filterByCategory filters bookmarked words by category`() = runTest {
        // Given
        val category = "noun"
        val nouns = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4", isBookmarked = true),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4", isBookmarked = true)
        )
        whenever(wordRepository.getBookmarkedWordsByCategory(category)).thenReturn(flowOf(nouns))

        // When
        viewModel.filterByCategory(category)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.filteredWords).isEqualTo(nouns)
    }

    @Test
    fun `test exportBookmarkedWords exports words to file`() = runTest {
        // Given
        val filePath = "bookmarks.csv"
        val bookmarkedWords = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4", isBookmarked = true)
        )
        whenever(wordRepository.getBookmarkedWords()).thenReturn(flowOf(bookmarkedWords))

        // When
        viewModel.exportBookmarkedWords(filePath)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(wordRepository).exportWordsToFile(bookmarkedWords, filePath)
    }

    @Test
    fun `test initial state is correct`() {
        // When initial state
        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.bookmarkedWords).isEmpty()
        assertThat(initialState.searchResults).isEmpty()
        assertThat(initialState.filteredWords).isEmpty()
        assertThat(initialState.error).isNull()
    }

    @Test
    fun `test error handling`() = runTest {
        // Given
        val errorMessage = "Failed to load bookmarked words"
        whenever(wordRepository.getBookmarkedWords()).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.loadBookmarkedWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.error).isEqualTo(errorMessage)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }
}
