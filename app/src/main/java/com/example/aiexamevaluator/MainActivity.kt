
package com.example.aiexamevaluator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiexamevaluator.navigation.AppNavGraph
import com.example.aiexamevaluator.ui.theme.AIExamEvaluatorTheme
import com.example.aiexamevaluator.viewmodel.ThemeViewModel
import com.example.aiexamevaluator.viewmodel.ThemeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(applicationContext)
            )
            val themeMode by themeViewModel.themeMode.collectAsState()

            AIExamEvaluatorTheme(
                themeMode = themeMode,
            ) {
                AppNavGraph()
            }
        }
    }
}

