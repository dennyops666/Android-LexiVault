package com.example.lexivault.performance

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lexivault.MainActivity
import com.example.lexivault.data.database.entity.WordEntity
import com.example.lexivault.data.repository.WordRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PerformanceTest {

    @get:Rule(order = 0)
    val benchmarkRule = BenchmarkRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var wordRepository: WordRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testListScrollPerformance() {
        // 准备大量测试数据
        runBlocking {
            for (i in 1..1000) {
                wordRepository.insertWord(
                    WordEntity(
                        id = i.toLong(),
                        word = "word$i",
                        meaning = "含义$i",
                        category = "noun",
                        type = "CET4",
                        createdAt = Instant.now().toEpochMilli()
                    ).toDomainModel()
                )
            }
        }

        benchmarkRule.measureRepeated {
            composeTestRule.onNodeWithTag("word_list")
                .performTouchInput {
                    swipeUp(startY = 1000f, endY = 100f)
                }
        }
    }

    @Test
    fun testSearchPerformance() {
        benchmarkRule.measureRepeated {
            composeTestRule.onNodeWithTag("search_bar").performClick()
            composeTestRule.onNodeWithTag("search_input")
                .performTextInput("test")
        }
    }

    @Test
    fun testWordCardAnimationPerformance() {
        benchmarkRule.measureRepeated {
            composeTestRule.onNodeWithTag("word_card")
                .performTouchInput {
                    swipeLeft()
                }
        }
    }

    @Test
    fun testDatabaseOperationPerformance() {
        benchmarkRule.measureRepeated {
            runBlocking {
                wordRepository.insertWord(
                    WordEntity(
                        id = System.currentTimeMillis(),
                        word = "performance_test",
                        meaning = "性能测试",
                        category = "noun",
                        type = "CET4",
                        createdAt = Instant.now().toEpochMilli()
                    ).toDomainModel()
                )
            }
        }
    }

    @Test
    fun testMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // 执行内存密集型操作
        runBlocking {
            for (i in 1..1000) {
                wordRepository.insertWord(
                    WordEntity(
                        id = i.toLong(),
                        word = "word$i",
                        meaning = "含义$i",
                        category = "noun",
                        type = "CET4",
                        createdAt = Instant.now().toEpochMilli()
                    ).toDomainModel()
                )
            }
        }

        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        println("Memory usage: ${(finalMemory - initialMemory) / 1024 / 1024} MB")
    }

    @Test
    fun testStartupTime() {
        benchmarkRule.measureRepeated {
            val intent = InstrumentationRegistry
                .getInstrumentation()
                .context
                .packageManager
                .getLaunchIntentForPackage(
                    InstrumentationRegistry
                        .getInstrumentation()
                        .targetContext
                        .packageName
                )
            InstrumentationRegistry
                .getInstrumentation()
                .startActivitySync(intent)
        }
    }

    @Test
    fun testImageLoadingPerformance() {
        benchmarkRule.measureRepeated {
            composeTestRule.onNodeWithTag("word_image")
                .performScrollTo()
        }
    }

    @Test
    fun testNetworkOperationPerformance() {
        benchmarkRule.measureRepeated {
            runBlocking {
                // 测试网络请求性能
                composeTestRule.onNodeWithTag("sync_button")
                    .performClick()
                // 等待同步完成
                composeTestRule.waitUntil(timeoutMillis = 5000) {
                    composeTestRule
                        .onAllNodesWithTag("sync_progress")
                        .fetchSemanticsNodes().isEmpty()
                }
            }
        }
    }

    @Test
    fun testAnimationFrameRate() {
        benchmarkRule.measureRepeated {
            composeTestRule.onNodeWithTag("animated_component")
                .performTouchInput {
                    swipeRight()
                }
            // 等待动画完成
            composeTestRule.mainClock.advanceTimeBy(500)
        }
    }
}
