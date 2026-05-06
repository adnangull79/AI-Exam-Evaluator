package com.example.aiexamevaluator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiexamevaluator.data.local.EvaluationHistoryEntity
import com.example.aiexamevaluator.ui.screens.EvaluationScreen
import com.example.aiexamevaluator.ui.screens.HomeScreen
import com.example.aiexamevaluator.ui.screens.HistoryScreen
import com.example.aiexamevaluator.ui.screens.ResultScreen
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.data.model.Question
import com.example.aiexamevaluator.viewmodel.EvaluationViewModel
import com.example.aiexamevaluator.viewmodel.EvaluationViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ── Convert EvaluationHistoryEntity → EvaluationResponse ──────────────────────
// Deserializes questionsJson back into List<Question> using Gson
private fun EvaluationHistoryEntity.toEvaluationResponse(): EvaluationResponse {
    val questionType = object : TypeToken<List<Question>>() {}.type
    val questions: List<Question> = try {
        Gson().fromJson(questionsJson, questionType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    return EvaluationResponse(
        totalObtained   = totalObtained,
        confidenceScore = confidenceScore,
        overallFeedback = overallFeedback,
        questions       = questions,
    )
}

@Composable
fun AppNavGraph() {
    var currentScreen by remember { mutableStateOf("home") }

    // Selected history entity for detail view
    var selectedEntity by remember { mutableStateOf<EvaluationHistoryEntity?>(null) }

    val context = LocalContext.current
    val viewModel: EvaluationViewModel = viewModel(
        factory = EvaluationViewModelFactory(context)
    )

    when (currentScreen) {

        "home" -> HomeScreen(
            onStartEvaluationClick = { currentScreen = "evaluation" },
            onHistoryClick         = { currentScreen = "history" },
        )

        "evaluation" -> EvaluationScreen(
            viewModel   = viewModel,
            onBackClick = { currentScreen = "home" },
        )

        "history" -> HistoryScreen(
            viewModel         = viewModel,
            onStartEvaluation = { currentScreen = "evaluation" },
            onHomeClick       = { currentScreen = "home" },
            onCardClick       = { entity ->
                selectedEntity = entity
                currentScreen  = "history_detail"
            }
        )

        "history_detail" -> {
            val entity = selectedEntity
            if (entity != null) {
                ResultScreen(
                    studentName       = entity.studentName,
                    rollNumber        = entity.rollNumber,
                    totalMarks        = entity.totalMarks,
                    result            = entity.toEvaluationResponse(),
                    // "Evaluate Another" repurposed as back to history
                    onEvaluateAnother = { currentScreen = "history" },
                )
            } else {
                // Safety fallback
                currentScreen = "history"
            }
        }

        else -> currentScreen = "home"
    }
}