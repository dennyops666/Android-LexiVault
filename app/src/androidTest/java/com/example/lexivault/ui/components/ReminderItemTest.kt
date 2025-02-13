package com.example.lexivault.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lexivault.data.database.entity.ReminderEntity
import com.example.lexivault.ui.screens.settings.ReminderItem
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

class ReminderItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testReminderItemDisplaysCorrectly() {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )

        // When
        composeTestRule.setContent {
            ReminderItem(
                reminder = reminder,
                onTimeChange = {},
                onToggle = {},
                onDelete = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("09:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("每日提醒").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("切换提醒").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("删除提醒").assertIsDisplayed()
    }

    @Test
    fun testReminderToggle() {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        var isEnabled = reminder.isEnabled

        // When
        composeTestRule.setContent {
            ReminderItem(
                reminder = reminder,
                onTimeChange = {},
                onToggle = { isEnabled = it },
                onDelete = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("切换提醒").performClick()
        assert(!isEnabled)
    }

    @Test
    fun testReminderDelete() {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        var isDeleted = false

        // When
        composeTestRule.setContent {
            ReminderItem(
                reminder = reminder,
                onTimeChange = {},
                onToggle = {},
                onDelete = { isDeleted = true }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("删除提醒").performClick()
        assert(isDeleted)
    }

    @Test
    fun testTimePickerDialog() {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = true,
            type = "DAILY"
        )
        var showDialog = false

        // When
        composeTestRule.setContent {
            ReminderItem(
                reminder = reminder,
                onTimeChange = { showDialog = true },
                onToggle = {},
                onDelete = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("09:00").performClick()
        assert(showDialog)
    }

    @Test
    fun testDisabledReminderAppearance() {
        // Given
        val reminder = ReminderEntity(
            id = 1,
            time = LocalTime.of(9, 0),
            isEnabled = false,
            type = "DAILY"
        )

        // When
        composeTestRule.setContent {
            ReminderItem(
                reminder = reminder,
                onTimeChange = {},
                onToggle = {},
                onDelete = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("09:00")
            .assertExists()
            .assertHasNoClickAction()
    }
}
