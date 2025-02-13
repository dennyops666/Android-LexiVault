package com.example.lexivault.di

import android.content.Context
import androidx.room.Room
import com.example.lexivault.data.database.WordDatabase
import com.example.lexivault.data.database.dao.WordBookDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.repository.WordBookRepository
import com.example.lexivault.data.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordDatabase(
        @ApplicationContext context: Context
    ): WordDatabase {
        return Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            "word_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: WordDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideWordBookDao(database: WordDatabase): WordBookDao {
        return database.wordBookDao()
    }

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }

    @Provides
    @Singleton
    fun provideWordBookRepository(wordBookDao: WordBookDao): WordBookRepository {
        return WordBookRepository(wordBookDao)
    }
}
