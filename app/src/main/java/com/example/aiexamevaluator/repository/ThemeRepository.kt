package com.example.aiexamevaluator.repository

import com.example.aiexamevaluator.data.local.ThemePreferences
import com.example.aiexamevaluator.theme.ThemeMode

class ThemeRepository(
    private val preferences: ThemePreferences
) {

    val themeMode = preferences.themeMode

    suspend fun saveTheme(themeMode: ThemeMode) {
        preferences.saveTheme(themeMode)
    }
}