package com.example.lexivault.data.repository

import com.example.lexivault.BuildConfig
import com.example.lexivault.data.api.ChatCompletionRequest
import com.example.lexivault.data.api.Message
import com.example.lexivault.data.api.OpenAIService
import javax.inject.Inject
import javax.inject.Singleton
import java.io.IOException
import java.net.HttpURLConnection
import retrofit2.HttpException

@Singleton
class AIRepository @Inject constructor(
    private val openAIService: OpenAIService
) {
    companion object {
        private const val API_KEY = "Bearer ${BuildConfig.OPENAI_API_KEY}"
    }

    private fun handleApiError(e: Exception): String {
        return when (e) {
            is IOException -> "网络连接错误，请稍后重试"
            is java.net.SocketTimeoutException -> "请求超时，请检查网络连接"
            is HttpException -> {
                when (e.code()) {
                    HttpURLConnection.HTTP_UNAUTHORIZED -> "API 认证失败，请检查配置"
                    429 -> "API 调用次数超限，请稍后重试"
                    else -> "服务器错误，请稍后重试"
                }
            }
            else -> "未知错误，请稍后重试"
        }
    }

    suspend fun generateMemoryTechnique(word: String, meaning: String): String {
        return try {
            val prompt = """
                为英语单词 "$word"（意思是：$meaning）创建一个生动的联想记忆方法。
                要求：
                1. 使用中文回答
                2. 联想要简短但生动形象
                3. 尽量结合单词的发音、形态或含义
            """.trimIndent()

            val response = openAIService.createChatCompletion(
                API_KEY,
                ChatCompletionRequest(
                    messages = listOf(
                        Message(role = "system", content = "你是一个专业的英语教师，擅长创造生动的单词记忆方法。"),
                        Message(role = "user", content = prompt)
                    )
                )
            )

            response.choices.firstOrNull()?.message?.content ?: "无法生成联想记忆方法"
        } catch (e: Exception) {
            handleApiError(e)
        }
    }

    suspend fun generateExample(word: String): String {
        return try {
            val prompt = """
                为英语单词 "$word" 创建一个简单的例句。
                要求：
                1. 使用中文回答
                2. 例句要简单易懂
                3. 例句要体现单词的常用含义
            """.trimIndent()

            val response = openAIService.createChatCompletion(
                API_KEY,
                ChatCompletionRequest(
                    messages = listOf(
                        Message(role = "system", content = "你是一个专业的英语教师，擅长创造简单的例句。"),
                        Message(role = "user", content = prompt)
                    )
                )
            )

            response.choices.firstOrNull()?.message?.content ?: "无法生成例句"
        } catch (e: Exception) {
            handleApiError(e)
        }
    }

    suspend fun recommendWords(learningHistory: List<String>): List<String> {
        return try {
            val prompt = """
                基于用户已学习的单词列表：${learningHistory.joinToString(", ")}
                推荐5个相关的英语单词。
                要求：
                1. 单词要与已学习的单词有关联
                2. 单词难度要适中
                3. 只返回英文单词，每行一个
            """.trimIndent()

            val response = openAIService.createChatCompletion(
                API_KEY,
                ChatCompletionRequest(
                    messages = listOf(
                        Message(role = "system", content = "你是一个专业的英语教师，擅长推荐相关单词。"),
                        Message(role = "user", content = prompt)
                    )
                )
            )

            response.choices.firstOrNull()?.message?.content?.split("\n")?.map { it.trim() }?.filter { it.isNotEmpty() }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
} 