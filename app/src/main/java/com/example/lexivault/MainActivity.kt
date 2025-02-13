package com.example.lexivault

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lexivault.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = (application as LexiVaultApplication).vocabularyRepository

        lifecycleScope.launch {
            val vocabularies = repository.getAllVocabularies().first()
            binding.textView.text = "Total vocabularies: ${vocabularies.size}"
        }
    }
}