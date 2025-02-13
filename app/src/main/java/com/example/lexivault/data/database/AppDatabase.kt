package com.example.lexivault.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lexivault.data.converter.DateTimeConverters
import com.example.lexivault.data.dao.VocabularyDao
import com.example.lexivault.data.dao.WordDao
import com.example.lexivault.data.entity.Vocabulary
import com.example.lexivault.data.entity.Word

@Database(
    entities = [
        Vocabulary::class,
        Word::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lexivault.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
