package com.example.lexivault.config

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
annotation class TestConfig
