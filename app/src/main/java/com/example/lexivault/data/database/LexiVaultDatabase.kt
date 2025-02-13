package com.example.lexivault.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lexivault.data.database.converter.Converters
import com.example.lexivault.data.database.dao.ReminderDao
import com.example.lexivault.data.database.dao.TestRecordDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.database.entity.ReminderEntity
import com.example.lexivault.data.database.entity.TestRecordEntity
import com.example.lexivault.data.database.entity.WordEntity

@Database(
    entities = [
        WordEntity::class,
        TestRecordEntity::class,
        ReminderEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class LexiVaultDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun testRecordDao(): TestRecordDao
    abstract fun reminderDao(): ReminderDao
}
