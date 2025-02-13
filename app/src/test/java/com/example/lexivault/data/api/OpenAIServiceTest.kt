package com.example.lexivault.data.api

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenAIServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var openAIService: OpenAIService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        openAIService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createChatCompletion should make correct request`() = runTest {
        // Given
        val apiKey = "Bearer test-api-key"
        val request = ChatCompletionRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                Message(
                    role = "user",
                    content = "Hello"
                )
            )
        )

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "Hi there! How can I help you today?"
                            },
                            "finishReason": "stop"
                        }
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val response = openAIService.createChatCompletion(apiKey, request)

        // Then
        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.method).isEqualTo("POST")
        assertThat(recordedRequest.path).isEqualTo("/v1/chat/completions")
        assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json")
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(apiKey)

        assertThat(response.choices).hasSize(1)
        assertThat(response.choices[0].message.role).isEqualTo("assistant")
        assertThat(response.choices[0].message.content).isEqualTo("Hi there! How can I help you today?")
        assertThat(response.choices[0].finishReason).isEqualTo("stop")
    }
}
