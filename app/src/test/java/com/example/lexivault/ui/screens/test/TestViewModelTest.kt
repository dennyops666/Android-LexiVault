package com.example.lexivault.ui.screens.test

import com.example.lexivault.data.database.entity.TestRecordEntity
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.TestRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TestViewModelTest {
    private lateinit var wordRepository: WordRepository
    private lateinit var testRepository: TestRepository
    private lateinit var viewModel: TestViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wordRepository = mock()
        testRepository = mock()
        viewModel = TestViewModel(wordRepository, testRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadTestWords loads words for testing`() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        whenever(wordRepository.getWordsForTesting()).thenReturn(flowOf(words))

        // When
        viewModel.loadTestWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.availableWords).isEqualTo(words)
        assertThat(viewModel.uiState.value.currentWord).isNotNull()
    }

    @Test
    fun `test checkAnswer with correct answer`() = runTest {
        // Given
        val word = WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4")
        whenever(wordRepository.getWordsForTesting()).thenReturn(flowOf(listOf(word)))
        viewModel.loadTestWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.checkAnswer("test")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.isAnswerCorrect).isTrue()
        verify(testRepository).saveTestRecord(any())
    }

    @Test
    fun `test checkAnswer with incorrect answer`() = runTest {
        // Given
        val word = WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4")
        whenever(wordRepository.getWordsForTesting()).thenReturn(flowOf(listOf(word)))
        viewModel.loadTestWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.checkAnswer("wrong")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.isAnswerCorrect).isFalse()
        verify(testRepository).saveTestRecord(any())
    }

    @Test
    fun `test nextQuestion moves to next word`() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4")
        )
        whenever(wordRepository.getWordsForTesting()).thenReturn(flowOf(words))
        viewModel.loadTestWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val firstWord = viewModel.uiState.value.currentWord
        viewModel.nextQuestion()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.currentWord).isNotEqualTo(firstWord)
        assertThat(viewModel.uiState.value.questionNumber).isEqualTo(2)
    }

    @Test
    fun `test generateOptions creates valid multiple choice options`() = runTest {
        // Given
        val words = listOf(
            WordEntity(id = 1, word = "test", meaning = "测试", category = "noun", type = "CET4"),
            WordEntity(id = 2, word = "example", meaning = "例子", category = "noun", type = "CET4"),
            WordEntity(id = 3, word = "practice", meaning = "练习", category = "verb", type = "CET4"),
            WordEntity(id = 4, word = "study", meaning = "学习", category = "verb", type = "CET4")
        )
        whenever(wordRepository.getWordsForTesting()).thenReturn(flowOf(words))
        viewModel.loadTestWords()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val options = viewModel.uiState.value.options

        // Then
        assertThat(options).hasSize(4)
        assertThat(options).contains(viewModel.uiState.value.currentWord?.meaning)
    }
}
