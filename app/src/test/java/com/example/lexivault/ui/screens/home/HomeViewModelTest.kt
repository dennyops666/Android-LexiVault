package com.example.lexivault.ui.screens.home

import com.example.lexivault.data.repository.WordRepository
import com.example.lexivault.data.repository.TestRepository
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var wordRepository: WordRepository
    private lateinit var testRepository: TestRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wordRepository = mock()
        testRepository = mock()
        viewModel = HomeViewModel(wordRepository, testRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadTodayProgress loads correct statistics`() = runTest {
        // Given
        val stats = mapOf(
            "todayLearned" to 10,
            "todayReviewed" to 5,
            "todayCorrect" to 8,
            "todayTotal" to 15
        )
        whenever(wordRepository.getTodayProgress()).thenReturn(flowOf(stats))

        // When
        viewModel.loadTodayProgress()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.todayLearned).isEqualTo(10)
        assertThat(viewModel.uiState.value.todayReviewed).isEqualTo(5)
        assertThat(viewModel.uiState.value.todayAccuracy).isEqualTo(8f / 15f)
    }

    @Test
    fun `test loadWeeklyProgress loads weekly data`() = runTest {
        // Given
        val weeklyData = mapOf(
            LocalDate.now().minusDays(6) to 5,
            LocalDate.now().minusDays(5) to 8,
            LocalDate.now().minusDays(4) to 10,
            LocalDate.now().minusDays(3) to 7,
            LocalDate.now().minusDays(2) to 12,
            LocalDate.now().minusDays(1) to 6,
            LocalDate.now() to 9
        )
        whenever(wordRepository.getWeeklyProgress()).thenReturn(flowOf(weeklyData))

        // When
        viewModel.loadWeeklyProgress()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.weeklyProgress).isEqualTo(weeklyData)
        assertThat(viewModel.uiState.value.weeklyAverage).isEqualTo(8.14f) // (5+8+10+7+12+6+9)/7
    }

    @Test
    fun `test loadTestResults loads test statistics`() = runTest {
        // Given
        val testStats = mapOf(
            "totalTests" to 50,
            "correctAnswers" to 40,
            "incorrectAnswers" to 10
        )
        whenever(testRepository.getTestStatistics()).thenReturn(flowOf(testStats))

        // When
        viewModel.loadTestResults()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.testAccuracy).isEqualTo(0.8f) // 40/50
        assertThat(viewModel.uiState.value.totalTests).isEqualTo(50)
    }

    @Test
    fun `test initial state is loading`() {
        // When initial state
        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.todayLearned).isEqualTo(0)
        assertThat(initialState.todayReviewed).isEqualTo(0)
        assertThat(initialState.todayAccuracy).isEqualTo(0f)
    }
}
