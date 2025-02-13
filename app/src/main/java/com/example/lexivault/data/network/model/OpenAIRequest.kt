package com.example.lexivault.data.network.model

data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 150
)

data class Message(
    val role: String,
    val content: String
)

// 用于生成助记和例句的提示模板
object PromptTemplates {
    fun generateMemoryTechnique(word: String, meaning: String): String =
        """作为一个英语教育专家，请为以下单词创建一个有效的记忆方法：
           |单词：$word
           |含义：$meaning
           |
           |请提供：
           |1. 词根词缀分析（如果适用）
           |2. 联想记忆法
           |3. 相关词汇
           |
           |请用中文回答，简洁明了。""".trimMargin()

    fun generateExampleSentences(word: String, meaning: String): String =
        """请为以下单词创建3个实用的例句：
           |单词：$word
           |含义：$meaning
           |
           |要求：
           |1. 例句应该体现单词的常见用法
           |2. 例句应该贴近日常生活
           |3. 难度适中，适合英语学习者
           |
           |请按以下格式回答：
           |1. [英文例句]
           |   [中文翻译]
           |2. [英文例句]
           |   [中文翻译]
           |3. [英文例句]
           |   [中文翻译]""".trimMargin()
}
