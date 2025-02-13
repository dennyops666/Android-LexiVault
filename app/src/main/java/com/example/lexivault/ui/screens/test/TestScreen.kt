package com.example.lexivault.ui.screens.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    onNavigateBack: () -> Unit,
    viewModel: TestScreenViewModel = hiltViewModel()
) {
    var answer by remember { mutableStateOf(TextFieldValue()) }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部栏
        TopAppBar(
            title = { Text("测试模式") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )

        // 显示当前进度
        LinearProgressIndicator(
            progress = uiState.progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // 显示题目
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.currentTest?.type) {
                    TestType.MULTIPLE_CHOICE -> {
                        Text(
                            text = uiState.currentTest.word,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        uiState.currentTest.options.forEach { option ->
                            Button(
                                onClick = { viewModel.submitAnswer(option) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(text = option)
                            }
                        }
                    }
                    TestType.SPELLING -> {
                        Text(
                            text = uiState.currentTest.meaning,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = answer,
                            onValueChange = { answer = it },
                            label = { Text("请输入单词") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.submitAnswer(answer.text)
                                answer = TextFieldValue()
                            }
                        ) {
                            Text("提交")
                        }
                    }
                    null -> {
                        Text("测试已完成！")
                        Text(
                            text = "正确率：${uiState.correctRate}%",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }

        // 显示反馈
        uiState.feedback?.let { feedback ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (feedback.isCorrect) {
                        true -> MaterialTheme.colorScheme.primaryContainer
                        false -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (feedback.isCorrect) "回答正确！" else "回答错误",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (!feedback.isCorrect) {
                        Text(
                            text = "正确答案：${feedback.correctAnswer}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { viewModel.skipCurrentTest() }
            ) {
                Text("跳过")
            }
            if (uiState.feedback != null) {
                Button(
                    onClick = { viewModel.nextTest() }
                ) {
                    Text("下一题")
                }
            }
        }
    }
}
