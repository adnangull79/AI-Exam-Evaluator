package com.example.aiexamevaluator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiexamevaluator.repository.ThemeRepository
import com.example.aiexamevaluator.theme.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> =
        repository.themeMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    fun updateTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.saveTheme(themeMode)
        }
    }
}