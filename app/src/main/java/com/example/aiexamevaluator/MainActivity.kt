package com.example.aiexamevaluator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aiexamevaluator.navigation.AppNavGraph
import com.example.aiexamevaluator.ui.theme.AIExamEvaluatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AIExamEvaluatorTheme {
                AppNavGraph()
            }
        }
    }
}

