package com.example.lexivault.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lexivault.data.repository.AIRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.common.truth.Truth.assertThat

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AIServiceIntegrationTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var aiRepository: AIRepository

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testNetworkTimeout() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setBodyDelay(5, TimeUnit.SECONDS)
                .setResponseCode(HttpURLConnection.HTTP_OK)
        )

        // When
        val result = aiRepository.generateMemoryTechnique("test", "测试")

        // Then
        assertThat(result).isEqualTo("请求超时，请检查网络连接")
    }

    @Test
    fun testInvalidApiKey() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .setBody("Invalid API key")
        )

        // When
        val result = aiRepository.generateMemoryTechnique("test", "测试")

        // Then
        assertThat(result).isEqualTo("API 认证失败，请检查配置")
    }

    @Test
    fun testRateLimit() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody("Rate limit exceeded")
        )

        // When
        val result = aiRepository.generateMemoryTechnique("test", "测试")

        // Then
        assertThat(result).isEqualTo("API 调用次数超限，请稍后重试")
    }

    @Test
    fun testServerError() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody("Internal server error")
        )

        // When
        val result = aiRepository.generateMemoryTechnique("test", "测试")

        // Then
        assertThat(result).isEqualTo("服务器错误，请稍后重试")
    }

    @Test
    fun testNetworkError() = runTest {
        // Given
        mockWebServer.shutdown()

        // When
        val result = aiRepository.generateMemoryTechnique("test", "测试")

        // Then
        assertThat(result).isEqualTo("网络连接错误，请稍后重试")
    }
}
