package com.example.aiexamevaluator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.aiexamevaluator.ui.screens.EvaluationScreen
import com.example.aiexamevaluator.ui.screens.HomeScreen

@Composable
fun AppNavGraph() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onStartEvaluationClick = { currentScreen = "evaluation" },
        )
        "evaluation" -> EvaluationScreen(
            onBackClick = { currentScreen = "home" },
        )
        else -> currentScreen = "home"
    }
}
