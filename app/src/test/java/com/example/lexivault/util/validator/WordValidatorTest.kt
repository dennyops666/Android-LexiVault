package com.example.lexivault.util.validator

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordValidatorTest {
    
    @Test
    fun `test validate word with valid input`() {
        // Given
        val word = "test"
        
        // When
        val result = WordValidator.validateWord(word)
        
        // Then
        assertThat(result.isSuccess).isTrue()
    }
    
    @Test
    fun `test validate word with empty input`() {
        // Given
        val word = ""
        
        // When
        val result = WordValidator.validateWord(word)
        
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("不能为空")
    }
    
    @Test
    fun `test validate word with too long input`() {
        // Given
        val word = "a".repeat(51) // 51 characters
        
        // When
        val result = WordValidator.validateWord(word)
        
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("不能超过50个字符")
    }
    
    @Test
    fun `test validate meaning with valid input`() {
        // Given
        val meaning = "测试"
        
        // When
        val result = WordValidator.validateMeaning(meaning)
        
        // Then
        assertThat(result.isSuccess).isTrue()
    }
    
    @Test
    fun `test validate meaning with empty input`() {
        // Given
        val meaning = ""
        
        // When
        val result = WordValidator.validateMeaning(meaning)
        
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("不能为空")
    }
    
    @Test
    fun `test validate meaning with too long input`() {
        // Given
        val meaning = "测".repeat(201) // 201 characters
        
        // When
        val result = WordValidator.validateMeaning(meaning)
        
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("不能超过200个字符")
    }
    
    @Test
    fun `test validate example with valid input`() {
        // Given
        val example = "This is a test example."
        
        // When
        val result = WordValidator.validateExample(example)
        
        // Then
        assertThat(result.isSuccess).isTrue()
    }
    
    @Test
    fun `test validate example with too long input`() {
        // Given
        val example = "a".repeat(501) // 501 characters
        
        // When
        val result = WordValidator.validateExample(example)
        
        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("不能超过500个字符")
    }
    
    @Test
    fun `test validate example with null input`() {
        // Given
        val example: String? = null
        
        // When
        val result = WordValidator.validateExample(example)
        
        // Then
        assertThat(result.isSuccess).isTrue()
    }
}
