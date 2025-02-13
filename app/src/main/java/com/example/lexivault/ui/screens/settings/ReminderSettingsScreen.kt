package com.example.lexivault.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clockState = rememberSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("提醒设置") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 每日提醒开关
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "每日学习提醒",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = uiState.isDailyReminderEnabled,
                onCheckedChange = { enabled ->
                    viewModel.setDailyReminder(enabled)
                }
            )
        }

        // 提醒时间设置
        if (uiState.isDailyReminderEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "提醒时间",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { clockState.show() }) {
                    Text(
                        text = String.format(
                            "%02d:%02d",
                            uiState.reminderHour,
                            uiState.reminderMinute
                        )
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // 复习计划说明
        Text(
            text = "复习计划",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "系统会自动为您安排以下复习计划：",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "• 学习1天后复习\n• 学习3天后复习\n• 学习7天后复习",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    ClockDialog(
        state = clockState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            viewModel.updateReminderTime(hours, minutes)
        }
    )
}
