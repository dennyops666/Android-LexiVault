package com.example.lexivault.ui.screens.memory

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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryViewModelTest {
    private lateinit var wordRepository: WordRepository
    private lateinit var viewModel: MemoryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wordRepository = mock()
        viewModel = MemoryViewModel(wordRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadWords loads words from repository`() = runTest {
        // Given
        val words = listOf(
            WordEntity(
                id = 1,
                word = "test",
                meaning = "测试",
                category = "noun",
                type = "CET4"
            ),
            WordEntity(
                id = 2,
                word = "example",
                meaning = "例子",
                category = "noun",
                type = "CET4"
            )
        )
        whenever(wordRepository.getAllWords()).thenReturn(flowOf(words))

        // When
        viewModel.loadWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.words.value).isEqualTo(words)
    }

    @Test
    fun `test toggleBookmark updates word bookmark status`() = runTest {
        // Given
        val wordId = 1L
        val isBookmarked = true

        // When
        viewModel.toggleBookmark(wordId, isBookmarked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(wordRepository).updateBookmarkStatus(wordId, isBookmarked)
    }

    @Test
    fun `test nextWord moves to next word`() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        whenever(wordRepository.getAllWords()).thenReturn(flowOf(words))
        viewModel.loadWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.nextWord()

        // Then
        assertThat(viewModel.currentWordIndex.value).isEqualTo(1)
    }

    @Test
    fun `test previousWord moves to previous word`() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        whenever(wordRepository.getAllWords()).thenReturn(flowOf(words))
        viewModel.loadWords()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.nextWord() // Move to second word

        // When
        viewModel.previousWord()

        // Then
        assertThat(viewModel.currentWordIndex.value).isEqualTo(0)
    }
}
