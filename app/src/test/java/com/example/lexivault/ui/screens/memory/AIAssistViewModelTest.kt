package com.example.lexivault.ui.screens.memory

import com.example.lexivault.data.repository.AIRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AIAssistViewModelTest {
    private lateinit var aiRepository: AIRepository
    private lateinit var viewModel: AIAssistViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        aiRepository = mock()
        viewModel = AIAssistViewModel(aiRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test generateMemoryTechnique generates technique successfully`() = runTest {
        // Given
        val word = "test"
        val meaning = "测试"
        val technique = "将test拆分为'特斯特'，联想到特斯拉在测试新产品"
        whenever(aiRepository.generateMemoryTechnique(word, meaning)).thenReturn(technique)

        // When
        viewModel.generateMemoryTechnique(word, meaning)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.memoryTechnique).isEqualTo(technique)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.error).isNull()
    }

    @Test
    fun `test generateExample generates example successfully`() = runTest {
        // Given
        val word = "test"
        val example = "We need to test this new feature before release."
        whenever(aiRepository.generateExample(word)).thenReturn(example)

        // When
        viewModel.generateExample(word)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.example).isEqualTo(example)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.error).isNull()
    }

    @Test
    fun `test generateMemoryTechnique handles error`() = runTest {
        // Given
        val word = "test"
        val meaning = "测试"
        val errorMessage = "网络连接错误，请稍后重试"
        whenever(aiRepository.generateMemoryTechnique(word, meaning)).thenReturn(errorMessage)

        // When
        viewModel.generateMemoryTechnique(word, meaning)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.error).isEqualTo(errorMessage)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `test generateExample handles error`() = runTest {
        // Given
        val word = "test"
        val errorMessage = "API 调用次数超限，请稍后重试"
        whenever(aiRepository.generateExample(word)).thenReturn(errorMessage)

        // When
        viewModel.generateExample(word)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.error).isEqualTo(errorMessage)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `test initial state is correct`() {
        // When initial state
        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.isLoading).isFalse()
        assertThat(initialState.memoryTechnique).isNull()
        assertThat(initialState.example).isNull()
        assertThat(initialState.error).isNull()
    }

    @Test
    fun `test clearError clears error state`() = runTest {
        // Given
        val errorMessage = "Some error"
        viewModel.updateError(errorMessage)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value.error).isEqualTo(errorMessage)

        // When
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.error).isNull()
    }
}
