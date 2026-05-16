package com.example.aiexamevaluator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.aiexamevaluator.ui.screens.WelcomeScreen
import com.example.aiexamevaluator.ui.screens.AuthSelectionScreen
import com.example.aiexamevaluator.ui.screens.SignUpScreen
import com.example.aiexamevaluator.ui.screens.LoginScreen
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.data.model.Question
import com.example.aiexamevaluator.viewmodel.AuthViewModel
import com.example.aiexamevaluator.viewmodel.AuthViewModelFactory
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
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())
    var currentScreen by remember {
        mutableStateOf("welcome")
    }

    LaunchedEffect(Unit) {
        if (authViewModel.uiState.value.isLoggedIn) {
            currentScreen = "home"
        }
    }

    // Selected history entity for detail view
    var selectedEntity by remember { mutableStateOf<EvaluationHistoryEntity?>(null) }

    val context = LocalContext.current
    val viewModel: EvaluationViewModel = viewModel(
        factory = EvaluationViewModelFactory(context)
    )

    when (currentScreen) {

        "welcome" -> WelcomeScreen(
            onGetStarted = { currentScreen = "auth" }
        )

        "auth" -> AuthSelectionScreen(
            onSignUpClick = { currentScreen = "signup" },
            onLoginClick = { currentScreen = "login" }
        )

        "signup" -> SignUpScreen(
            authViewModel = authViewModel,
            onBackClick = { currentScreen = "auth" },
            onCreateAccountClick = { currentScreen = "home" },
            onLoginClick = { currentScreen = "login" }
        )

        "login" -> LoginScreen(
            authViewModel = authViewModel,
            onBackClick = { currentScreen = "auth" },
            onLoginClick = { currentScreen = "home" },
            onSignUpClick = { currentScreen = "signup" }
        )

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
                    evaluatedAt       = entity.evaluatedAt,   // ← add this
                    onEvaluateAnother = { currentScreen = "history" },
                )
            } else { currentScreen = "history" }
        }

        else -> currentScreen = "home"
    }
}