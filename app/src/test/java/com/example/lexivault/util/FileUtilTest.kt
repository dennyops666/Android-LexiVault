package com.example.lexivault.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class FileUtilTest {
    private lateinit var context: Context
    private lateinit var testDir: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        testDir = context.getDir("test_files", Context.MODE_PRIVATE)
        testDir.mkdirs()
    }

    @Test
    fun `createFile should create new file`() {
        // Given
        val filename = "test.txt"
        val file = File(testDir, filename)
        
        // When
        val result = FileUtil.createFile(file.absolutePath)
        
        // Then
        assertThat(result).isTrue()
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun `writeToFile should write content`() {
        // Given
        val file = File(testDir, "write_test.txt")
        val content = "Test content"
        
        // When
        val result = FileUtil.writeToFile(file.absolutePath, content)
        
        // Then
        assertThat(result).isTrue()
        assertThat(file.readText()).isEqualTo(content)
    }

    @Test
    fun `readFromFile should read content`() {
        // Given
        val file = File(testDir, "read_test.txt")
        val content = "Test content"
        file.writeText(content)
        
        // When
        val result = FileUtil.readFromFile(file.absolutePath)
        
        // Then
        assertThat(result).isEqualTo(content)
    }

    @Test
    fun `deleteFile should remove file`() {
        // Given
        val file = File(testDir, "delete_test.txt")
        file.createNewFile()
        
        // When
        val result = FileUtil.deleteFile(file.absolutePath)
        
        // Then
        assertThat(result).isTrue()
        assertThat(file.exists()).isFalse()
    }

    @Test
    fun `getFileExtension should return correct extension`() {
        // Given
        val filename = "test.txt"
        
        // When
        val result = FileUtil.getFileExtension(filename)
        
        // Then
        assertThat(result).isEqualTo("txt")
    }

    @Test
    fun `isValidFile should validate file`() {
        // Given
        val validFile = File(testDir, "valid.txt")
        validFile.createNewFile()
        val invalidFile = File(testDir, "invalid.txt")
        
        // When & Then
        assertThat(FileUtil.isValidFile(validFile.absolutePath)).isTrue()
        assertThat(FileUtil.isValidFile(invalidFile.absolutePath)).isFalse()
    }

    @Test
    fun `getFileSize should return correct size`() {
        // Given
        val file = File(testDir, "size_test.txt")
        val content = "Test content"
        file.writeText(content)
        
        // When
        val result = FileUtil.getFileSize(file.absolutePath)
        
        // Then
        assertThat(result).isEqualTo(content.length.toLong())
    }

    @Test
    fun `createDirectory should create new directory`() {
        // Given
        val dirPath = File(testDir, "new_dir").absolutePath
        
        // When
        val result = FileUtil.createDirectory(dirPath)
        
        // Then
        assertThat(result).isTrue()
        assertThat(File(dirPath).exists()).isTrue()
        assertThat(File(dirPath).isDirectory).isTrue()
    }

    @Test
    fun `listFiles should return file list`() {
        // Given
        val dir = File(testDir, "list_dir")
        dir.mkdirs()
        File(dir, "file1.txt").createNewFile()
        File(dir, "file2.txt").createNewFile()
        
        // When
        val result = FileUtil.listFiles(dir.absolutePath)
        
        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("file1.txt", "file2.txt")
    }

    @Test
    fun `copyFile should copy content`() {
        // Given
        val sourceFile = File(testDir, "source.txt")
        val targetFile = File(testDir, "target.txt")
        val content = "Test content"
        sourceFile.writeText(content)
        
        // When
        val result = FileUtil.copyFile(sourceFile.absolutePath, targetFile.absolutePath)
        
        // Then
        assertThat(result).isTrue()
        assertThat(targetFile.exists()).isTrue()
        assertThat(targetFile.readText()).isEqualTo(content)
    }
}
