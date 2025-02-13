package com.example.lexivault.di

import android.content.Context
import com.example.lexivault.data.database.dao.ReminderDao
import com.example.lexivault.data.database.dao.WordDao
import com.example.lexivault.data.repository.ReminderRepository
import com.example.lexivault.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.mockito.kotlin.mock
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideReminderDao(): ReminderDao = mock()

    @Provides
    @Singleton
    fun provideWordDao(): WordDao = mock()

    @Provides
    @Singleton
    fun provideNotificationHelper(): NotificationHelper = mock()

    @Provides
    @Singleton
    fun provideReminderRepository(
        reminderDao: ReminderDao,
        wordDao: WordDao,
        notificationHelper: NotificationHelper
    ): ReminderRepository = ReminderRepository(reminderDao, wordDao, notificationHelper)
}
