package com.example.lexivault.di

import android.content.Context
import androidx.room.Room
import com.example.lexivault.data.database.LexiVaultDatabase
import com.example.lexivault.data.database.dao.ReminderSettingsDao
import com.example.lexivault.data.database.dao.VocabularyDao
import com.example.lexivault.data.database.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LexiVaultDatabase {
        return Room.databaseBuilder(
            context,
            LexiVaultDatabase::class.java,
            LexiVaultDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideVocabularyDao(database: LexiVaultDatabase): VocabularyDao {
        return database.vocabularyDao()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: LexiVaultDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideReminderSettingsDao(database: LexiVaultDatabase): ReminderSettingsDao {
        return database.reminderSettingsDao()
    }
} 