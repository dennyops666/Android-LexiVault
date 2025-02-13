package com.example.lexivault.di

import com.example.lexivault.data.database.dao.ReminderSettingsDao
import com.example.lexivault.data.database.dao.VocabularyDao
import com.example.lexivault.data.database.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideVocabularyDao(): VocabularyDao = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideWordDao(): WordDao = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideReminderSettingsDao(): ReminderSettingsDao = mockk(relaxed = true)
}
