package com.example.lexivault.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SecureStorageTest {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var secureStorage: SecureStorage

    @Before
    fun setup() {
        context = mock()
        sharedPreferences = mock()
        secureStorage = SecureStorage(context)

        // Mock EncryptedSharedPreferences creation
        whenever(context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)
    }

    @Test
    fun `test saveString saves encrypted string`() {
        // Given
        val key = "test_key"
        val value = "test_value"
        val editor: SharedPreferences.Editor = mock()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(key, value)).thenReturn(editor)
        whenever(editor.apply()).then { }

        // When
        secureStorage.saveString(key, value)

        // Then
        verify(editor).putString(key, value)
        verify(editor).apply()
    }

    @Test
    fun `test getString retrieves encrypted string`() {
        // Given
        val key = "test_key"
        val value = "test_value"
        whenever(sharedPreferences.getString(key, null)).thenReturn(value)

        // When
        val result = secureStorage.getString(key)

        // Then
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun `test getString returns null for non-existent key`() {
        // Given
        val key = "non_existent_key"
        whenever(sharedPreferences.getString(key, null)).thenReturn(null)

        // When
        val result = secureStorage.getString(key)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `test removeKey removes value`() {
        // Given
        val key = "test_key"
        val editor: SharedPreferences.Editor = mock()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.remove(key)).thenReturn(editor)
        whenever(editor.apply()).then { }

        // When
        secureStorage.removeKey(key)

        // Then
        verify(editor).remove(key)
        verify(editor).apply()
    }
}
