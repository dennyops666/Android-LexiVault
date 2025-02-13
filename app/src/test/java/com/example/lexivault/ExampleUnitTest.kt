package com.example.lexivault

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.google.common.truth.Truth.assertThat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
    @Test
    fun `basic arithmetic operations work correctly`() {
        // Given
        val a = 2
        val b = 2
        
        // When
        val sum = a + b
        
        // Then
        assertThat(sum).isEqualTo(4)
    }
}