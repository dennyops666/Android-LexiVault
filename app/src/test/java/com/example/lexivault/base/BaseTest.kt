package com.example.lexivault.base

import com.example.lexivault.config.TestConfig
import com.example.lexivault.rule.MainCoroutineRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
@TestConfig
abstract class BaseTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun baseSetup() {
        MockKAnnotations.init(this)
    }
}
