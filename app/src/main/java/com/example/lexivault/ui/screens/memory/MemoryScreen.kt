package com.example.lexivault.ui.screens.memory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: MemoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAIAssist by remember { mutableStateOf(false) }
    var isCardFlipped by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        TopAppBar(
            title = { Text("记忆模式") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 单词卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { isCardFlipped = !isCardFlipped },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.currentWord == null) {
                    Text("暂无单词")
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (!isCardFlipped) {
                            Text(
                                text = uiState.currentWord.word,
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "点击查看释义",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = uiState.currentWord.meaning,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            if (!uiState.currentWord.example.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "例句：${uiState.currentWord.example}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { viewModel.previousWord() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "上一个")
            }

            IconButton(
                onClick = { 
                    if (uiState.currentWord != null) {
                        showAIAssist = true
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = "AI 助手")
            }

            IconButton(
                onClick = { viewModel.nextWord() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "下一个")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.markAsKnown() },
                enabled = uiState.currentWord != null
            ) {
                Text("已掌握")
            }

            Button(
                onClick = { viewModel.addToWordBook() },
                enabled = uiState.currentWord != null
            ) {
                Text("添加到生词本")
            }
        }
    }

    // AI 辅助对话框
    if (showAIAssist && uiState.currentWord != null) {
        AIAssistDialog(
            word = uiState.currentWord!!,
            onDismiss = { showAIAssist = false }
        )
    }
}
