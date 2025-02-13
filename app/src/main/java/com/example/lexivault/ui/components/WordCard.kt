package com.example.lexivault.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.lexivault.data.database.entity.WordEntity
import kotlin.math.abs

@Composable
fun WordCard(
    word: WordEntity,
    onBookmark: (Boolean) -> Unit,
    onNextWord: () -> Unit,
    onPreviousWord: () -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .graphicsLayer {
                rotationY = rotation
                translationX = offsetX
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > 100) {
                            if (offsetX > 0) onPreviousWord()
                            else onNextWord()
                        }
                        offsetX = 0f
                    },
                    onDragCancel = { offsetX = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // 正面显示单词
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (word.phonetic != null) {
                        Text(
                            text = word.phonetic,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // 背面显示释义
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                ) {
                    Text(
                        text = word.meaning,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (word.example != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = word.example,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // 生词本按钮
        IconButton(
            onClick = { onBookmark(!word.isBookmarked) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = if (word.isBookmarked) {
                    androidx.compose.material.icons.Icons.Filled.Bookmark
                } else {
                    androidx.compose.material.icons.Icons.Outlined.BookmarkBorder
                },
                contentDescription = if (word.isBookmarked) "Remove from wordbook" else "Add to wordbook"
            )
        }

        // 翻转提示
        TextButton(
            onClick = { isFlipped = !isFlipped },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = if (isFlipped) "查看单词" else "查看释义")
        }
    }
}
