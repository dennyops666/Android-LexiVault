package com.example.lexivault.util

object StringUtil {
    fun capitalizeFirstLetter(input: String): String {
        return if (input.isEmpty()) input else input.substring(0, 1).uppercase() + input.substring(1)
    }

    fun truncate(input: String, maxLength: Int): String {
        return if (input.length <= maxLength) input
        else input.substring(0, maxLength - 3) + "..."
    }

    fun removeExtraSpaces(input: String): String {
        return input.trim().replace("\\s+".toRegex(), " ")
    }

    fun isValidWord(word: String): Boolean {
        return word.matches("^[a-zA-Z0-9-]+$".toRegex())
    }

    fun formatMeaning(meaning: String): String {
        return meaning.split("(?<=\\d\\.)\\s+".toRegex())
            .joinToString("\n")
            .trim()
    }

    fun extractKeywords(input: String): List<String> {
        val stopWords = setOf("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with")
        return input.lowercase()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() && !stopWords.contains(it) }
    }

    fun normalizeWord(input: String): String {
        return input.trim().lowercase()
    }

    fun splitSentence(input: String): List<String> {
        return input.split("\\s+".toRegex())
            .filter { it.isNotEmpty() }
            .map { it.trim('.', ',', '!', '?', ';', ':') }
    }
} 