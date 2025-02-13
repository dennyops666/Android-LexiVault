package com.example.lexivault.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexivault.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderSettingsViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderSettingsState())
    val uiState: StateFlow<ReminderSettingsState> = _uiState.asStateFlow()

    fun setDailyReminder(enabled: Boolean, hour: Int = 9, minute: Int = 0) {
        viewModelScope.launch {
            if (enabled) {
                reminderRepository.setDailyReminder(hour, minute)
            } else {
                reminderRepository.cancelDailyReminder()
            }
            _uiState.value = _uiState.value.copy(
                isDailyReminderEnabled = enabled,
                reminderHour = hour,
                reminderMinute = minute
            )
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        if (_uiState.value.isDailyReminderEnabled) {
            viewModelScope.launch {
                reminderRepository.setDailyReminder(hour, minute)
                _uiState.value = _uiState.value.copy(
                    reminderHour = hour,
                    reminderMinute = minute
                )
            }
        }
    }
}

data class ReminderSettingsState(
    val isDailyReminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0
)
