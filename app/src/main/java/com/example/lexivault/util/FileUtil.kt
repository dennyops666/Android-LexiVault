package com.example.lexivault.util

import android.content.Context
import android.net.Uri
import java.io.*

object FileUtil {
    fun writeToFile(context: Context, fileName: String, content: String): Boolean {
        return try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                fos.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun readFromFile(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.joinToString("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        return try {
            context.deleteFile(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun copyFile(context: Context, sourceUri: Uri, destFileName: String): Boolean {
        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                context.openFileOutput(destFileName, Context.MODE_PRIVATE).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getFileSize(context: Context, fileName: String): Long {
        return try {
            val file = File(context.filesDir, fileName)
            file.length()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun listFiles(context: Context): List<String> {
        return try {
            context.fileList().toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 