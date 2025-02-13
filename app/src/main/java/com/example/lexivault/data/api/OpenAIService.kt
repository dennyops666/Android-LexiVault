package com.example.lexivault.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers(
        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message,
    val finishReason: String? = null
)

data class Message(
    val role: String,
    val content: String
)
