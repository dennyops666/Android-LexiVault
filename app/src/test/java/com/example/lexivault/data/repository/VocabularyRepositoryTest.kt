package com.example.lexivault.data.repository

import com.example.lexivault.data.dao.VocabularyDao
import com.example.lexivault.data.dao.WordDao
import com.example.lexivault.util.TestUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VocabularyRepositoryTest {
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var vocabularyDao: VocabularyDao
    private lateinit var wordDao: WordDao
    private lateinit var repository: VocabularyRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        vocabularyDao = mockk(relaxed = true)
        wordDao = mockk(relaxed = true)
        repository = VocabularyRepository(vocabularyDao, wordDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getAllVocabularies should return all vocabularies`() = runTest {
        // Given
        val vocabulary = TestUtil.createVocabulary()
        coEvery { vocabularyDao.getAllVocabularies() } returns flowOf(listOf(vocabulary))

        // When
        val result = repository.getAllVocabularies().first()

        // Then
        assertEquals(listOf(vocabulary), result)
        coVerify { vocabularyDao.getAllVocabularies() }
    }

    @Test
    fun `test getVocabularyById should return vocabulary by id`() = runTest {
        // Given
        val vocabulary = TestUtil.createVocabulary()
        coEvery { vocabularyDao.getVocabularyById(1L) } returns flowOf(vocabulary)

        // When
        val result = repository.getVocabularyById(1L).first()

        // Then
        assertEquals(vocabulary, result)
        coVerify { vocabularyDao.getVocabularyById(1L) }
    }

    @Test
    fun `test insertVocabulary should insert vocabulary`() = runTest {
        // Given
        val vocabulary = TestUtil.createVocabulary()
        coEvery { vocabularyDao.insertVocabulary(vocabulary) } returns 1L

        // When
        val result = repository.insertVocabulary(vocabulary)

        // Then
        assertEquals(1L, result)
        coVerify { vocabularyDao.insertVocabulary(vocabulary) }
    }

    @Test
    fun `test updateVocabulary should update vocabulary`() = runTest {
        // Given
        val vocabulary = TestUtil.createVocabulary()
        coEvery { vocabularyDao.updateVocabulary(vocabulary) } returns Unit

        // When
        repository.updateVocabulary(vocabulary)

        // Then
        coVerify { vocabularyDao.updateVocabulary(vocabulary) }
    }

    @Test
    fun `test deleteVocabulary should delete vocabulary and its words`() = runTest {
        // Given
        val vocabulary = TestUtil.createVocabulary()
        coEvery { vocabularyDao.deleteVocabulary(vocabulary) } returns Unit
        coEvery { wordDao.deleteWordsByVocabularyId(vocabulary.id) } returns Unit

        // When
        repository.deleteVocabulary(vocabulary)

        // Then
        coVerify { vocabularyDao.deleteVocabulary(vocabulary) }
        coVerify { wordDao.deleteWordsByVocabularyId(vocabulary.id) }
    }

    @Test
    fun `test searchVocabularies should return filtered vocabularies`() = runTest {
        // Given
        val query = "CET"
        val vocabulary = TestUtil.createVocabulary()
        coEvery { vocabularyDao.searchVocabularies("%$query%") } returns flowOf(listOf(vocabulary))

        // When
        val result = repository.searchVocabularies(query).first()

        // Then
        assertEquals(listOf(vocabulary), result)
        coVerify { vocabularyDao.searchVocabularies("%$query%") }
    }
}
