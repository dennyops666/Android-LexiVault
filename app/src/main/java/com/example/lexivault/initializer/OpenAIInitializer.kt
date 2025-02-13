package com.example.lexivault.initializer

import android.content.Context
import androidx.startup.Initializer
import com.example.lexivault.BuildConfig
import com.example.lexivault.util.SecureStorage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class OpenAIInitializer : Initializer<Unit> {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface OpenAIInitializerEntryPoint {
        fun secureStorage(): SecureStorage
    }

    override fun create(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            OpenAIInitializerEntryPoint::class.java
        )
        
        val secureStorage = entryPoint.secureStorage()
        if (secureStorage.getApiKey() == null) {
            // API Key should be provided through BuildConfig or environment variables
            // For development, you can set it in local.properties:
            // openai.api.key=your_api_key_here
            secureStorage.saveApiKey(BuildConfig.OPENAI_API_KEY)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
