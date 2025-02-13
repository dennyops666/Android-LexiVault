package com.example.lexivault

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(Application::class)
interface TestLexiVaultApplication
