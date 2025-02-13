package com.example.lexivault

import android.app.Application
import androidx.room.Room
import com.example.lexivault.data.database.LexiVaultDatabase
import com.example.lexivault.data.repository.VocabularyRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LexiVaultApplication : Application() {
    private lateinit var database: LexiVaultDatabase
    lateinit var vocabularyRepository: VocabularyRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            LexiVaultDatabase::class.java,
            LexiVaultDatabase.DATABASE_NAME
        ).build()
        vocabularyRepository = VocabularyRepository(
            vocabularyDao = database.vocabularyDao(),
            wordDao = database.wordDao()
        )
    }
}
