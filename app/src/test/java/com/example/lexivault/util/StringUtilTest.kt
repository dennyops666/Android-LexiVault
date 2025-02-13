package com.example.lexivault.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringUtilTest {
    @Test
    fun `capitalizeFirstLetter should capitalize first letter`() {
        // Given
        val input = "hello"
        
        // When
        val result = StringUtil.capitalizeFirstLetter(input)
        
        // Then
        assertThat(result).isEqualTo("Hello")
    }

    @Test
    fun `capitalizeFirstLetter should handle empty string`() {
        // Given
        val input = ""
        
        // When
        val result = StringUtil.capitalizeFirstLetter(input)
        
        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `truncate should limit string length`() {
        // Given
        val input = "This is a very long string that needs to be truncated"
        val maxLength = 20
        
        // When
        val result = StringUtil.truncate(input, maxLength)
        
        // Then
        assertThat(result.length).isAtMost(maxLength)
        assertThat(result).endsWith("...")
    }

    @Test
    fun `truncate should not modify short strings`() {
        // Given
        val input = "Short string"
        val maxLength = 20
        
        // When
        val result = StringUtil.truncate(input, maxLength)
        
        // Then
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `removeExtraSpaces should clean string`() {
        // Given
        val input = "  This   has   extra   spaces  "
        
        // When
        val result = StringUtil.removeExtraSpaces(input)
        
        // Then
        assertThat(result).isEqualTo("This has extra spaces")
    }

    @Test
    fun `isValidWord should validate word format`() {
        // Given
        val validWord = "test-word"
        val invalidWord = "test@word"
        
        // When & Then
        assertThat(StringUtil.isValidWord(validWord)).isTrue()
        assertThat(StringUtil.isValidWord(invalidWord)).isFalse()
    }

    @Test
    fun `formatMeaning should format meaning correctly`() {
        // Given
        val meaning = "1. first meaning 2. second meaning"
        
        // When
        val result = StringUtil.formatMeaning(meaning)
        
        // Then
        assertThat(result).contains("\n")
        assertThat(result).startsWith("1.")
        assertThat(result).contains("2.")
    }

    @Test
    fun `extractKeywords should get important words`() {
        // Given
        val input = "The quick brown fox jumps over the lazy dog"
        
        // When
        val result = StringUtil.extractKeywords(input)
        
        // Then
        assertThat(result).contains("quick")
        assertThat(result).contains("brown")
        assertThat(result).contains("fox")
        assertThat(result).doesNotContain("the")
    }

    @Test
    fun `normalizeWord should standardize word format`() {
        // Given
        val input = "  Test-Word  "
        
        // When
        val result = StringUtil.normalizeWord(input)
        
        // Then
        assertThat(result).isEqualTo("test-word")
    }

    @Test
    fun `splitSentence should split correctly`() {
        // Given
        val input = "This is a test sentence."
        
        // When
        val result = StringUtil.splitSentence(input)
        
        // Then
        assertThat(result).hasSize(5)
        assertThat(result).contains("test")
    }
}
