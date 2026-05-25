package com.example.aiexamevaluator.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aiexamevaluator.data.local.ThemePreferences
import com.example.aiexamevaluator.repository.ThemeRepository

class ThemeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            val repository = ThemeRepository(ThemePreferences(context))
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}