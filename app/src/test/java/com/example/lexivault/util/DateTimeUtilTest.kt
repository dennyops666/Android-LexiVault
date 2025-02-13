package com.example.lexivault.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DateTimeUtilTest {
    @Test
    fun `formatDate should return correct format`() {
        // Given
        val date = LocalDate.of(2025, 2, 13)
        
        // When
        val result = DateTimeUtil.formatDate(date)
        
        // Then
        assertThat(result).isEqualTo("2025-02-13")
    }

    @Test
    fun `formatTime should return correct format`() {
        // Given
        val time = LocalTime.of(20, 30)
        
        // When
        val result = DateTimeUtil.formatTime(time)
        
        // Then
        assertThat(result).isEqualTo("20:30")
    }

    @Test
    fun `parseDateTime should return correct LocalDateTime`() {
        // Given
        val dateTimeStr = "2025-02-13 20:30:00"
        
        // When
        val result = DateTimeUtil.parseDateTime(dateTimeStr)
        
        // Then
        assertThat(result).isEqualTo(
            LocalDateTime.of(2025, 2, 13, 20, 30, 0)
        )
    }

    @Test
    fun `calculateNextReminder should return correct next reminder time`() {
        // Given
        val now = LocalDateTime.of(2025, 2, 13, 15, 0)
        val reminderTime = LocalTime.of(20, 0)
        val reminderDays = listOf(1, 2, 3, 4, 5) // 周一到周五
        
        // When
        val result = DateTimeUtil.calculateNextReminder(now, reminderTime, reminderDays)
        
        // Then
        assertThat(result).isNotNull()
        assertThat(result.toLocalTime()).isEqualTo(reminderTime)
        assertThat(result.isAfter(now)).isTrue()
    }

    @Test
    fun `isTimeToRemind should return true when it's time`() {
        // Given
        val now = LocalDateTime.of(2025, 2, 13, 20, 0)
        val reminderTime = LocalTime.of(20, 0)
        val reminderDays = listOf(4) // 周四
        
        // When
        val result = DateTimeUtil.isTimeToRemind(now, reminderTime, reminderDays)
        
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isTimeToRemind should return false when it's not time`() {
        // Given
        val now = LocalDateTime.of(2025, 2, 13, 19, 59)
        val reminderTime = LocalTime.of(20, 0)
        val reminderDays = listOf(4) // 周四
        
        // When
        val result = DateTimeUtil.isTimeToRemind(now, reminderTime, reminderDays)
        
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getDaysBetween should return correct number of days`() {
        // Given
        val start = LocalDate.of(2025, 2, 13)
        val end = LocalDate.of(2025, 2, 20)
        
        // When
        val result = DateTimeUtil.getDaysBetween(start, end)
        
        // Then
        assertThat(result).isEqualTo(7)
    }

    @Test
    fun `getMinutesBetween should return correct number of minutes`() {
        // Given
        val start = LocalDateTime.of(2025, 2, 13, 20, 0)
        val end = LocalDateTime.of(2025, 2, 13, 20, 30)
        
        // When
        val result = DateTimeUtil.getMinutesBetween(start, end)
        
        // Then
        assertThat(result).isEqualTo(30)
    }
}
