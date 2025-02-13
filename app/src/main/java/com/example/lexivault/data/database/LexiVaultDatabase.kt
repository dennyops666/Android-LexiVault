package com.example.lexivault.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lexivault.data.database.converter.Converters
import com.example.lexivault.data.database.dao.ReminderSettingsDao
import com.example.lexivault.data.database.dao.VocabularyDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.ReminderSettingsEntity
import com.example.lexivault.data.database.entity.VocabularyEntity
import com.example.lexivault.data.database.entity.WordEntity

@Database(
    entities = [
        VocabularyEntity::class,
        WordEntity::class,
        ReminderSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LexiVaultDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun wordDao(): WordDao
    abstract fun reminderSettingsDao(): ReminderSettingsDao

    companion object {
        const val DATABASE_NAME = "lexivault.db"
    }
}
