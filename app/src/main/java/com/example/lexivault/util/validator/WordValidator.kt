package com.example.lexivault.util.validator

object WordValidator {
    fun validateWord(word: String?): Result<Unit> {
        return try {
            when {
                word == null || word.isEmpty() -> throw IllegalArgumentException("单词不能为空")
                word.length > 50 -> throw IllegalArgumentException("单词不能超过50个字符")
                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun validateMeaning(meaning: String?): Result<Unit> {
        return try {
            when {
                meaning == null || meaning.isEmpty() -> throw IllegalArgumentException("释义不能为空")
                meaning.length > 200 -> throw IllegalArgumentException("释义不能超过200个字符")
                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun validateExample(example: String?): Result<Unit> {
        return try {
            when {
                example == null -> Result.success(Unit)
                example.length > 500 -> throw IllegalArgumentException("例句不能超过500个字符")
                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 