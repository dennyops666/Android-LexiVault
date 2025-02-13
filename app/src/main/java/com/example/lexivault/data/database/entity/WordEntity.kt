package com.example.lexivault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,                 // 单词
    val meaning: String,             // 释义
    val phonetic: String? = null,    // 音标（可选）
    val example: String? = null,     // 例句（可选）
    val category: String,            // 词性分类（如动词、名词等）
    val type: String,               // 用途分类（如四级、六级等）
    val isBookmarked: Boolean = false, // 是否加入生词本
    val reviewCount: Int = 0,        // 复习次数
    val lastReviewTime: Long = 0,    // 最后复习时间
    val createdAt: Long = System.currentTimeMillis() // 创建时间
)
