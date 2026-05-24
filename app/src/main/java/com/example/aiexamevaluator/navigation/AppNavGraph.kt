package com.example.aiexamevaluator.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiexamevaluator.data.local.EvaluationHistoryEntity
import com.example.aiexamevaluator.ui.screens.EditProfileScreen
import com.example.aiexamevaluator.ui.screens.EvaluationScreen
import com.example.aiexamevaluator.ui.screens.AboutScreen
import com.example.aiexamevaluator.ui.screens.HomeScreen
import com.example.aiexamevaluator.ui.screens.FaqScreen
import com.example.aiexamevaluator.ui.screens.HistoryScreen
import com.example.aiexamevaluator.ui.screens.ResultScreen
import com.example.aiexamevaluator.ui.screens.SettingsScreen
import com.example.aiexamevaluator.ui.screens.SplashScreen
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
        mutableStateOf("splash")
    }
    var resultSource by remember {
        mutableStateOf("evaluation")
    }

    // Selected history entity for detail view
    var selectedEntity by remember { mutableStateOf<EvaluationHistoryEntity?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity
    val viewModel: EvaluationViewModel = viewModel(
        factory = EvaluationViewModelFactory(context)
    )

    BackHandler {
        when (currentScreen) {
            "splash" -> Unit

            "welcome" -> activity?.finish()

            "auth" -> currentScreen = "welcome"

            "login" -> currentScreen = "auth"

            "signup" -> currentScreen = "auth"

            "home" -> activity?.finish()

            "evaluation" -> currentScreen = "home"

            "history" -> currentScreen = "home"

            "settings" -> currentScreen = "home"

            "history_detail" -> currentScreen = "history"

            "about" -> currentScreen = "settings"

            "faq" -> currentScreen = "settings"

            else -> Unit
        }
    }

    when (currentScreen) {

        "splash" -> SplashScreen(
            onSplashFinished = {
                if (authViewModel.uiState.value.isLoggedIn) {
                    currentScreen = "home"
                } else {
                    currentScreen = "welcome"
                }
            }
        )

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
            onStartEvaluationClick = {
                resultSource = "evaluation"
                currentScreen = "evaluation"
            },
            onHistoryClick         = { currentScreen = "history" },
            onSettingsClick        = { currentScreen = "settings" },
        )

        "evaluation" -> EvaluationScreen(
            viewModel   = viewModel,
            onBackClick = { currentScreen = "home" },
        )

        "history" -> HistoryScreen(
            viewModel         = viewModel,
            onStartEvaluation = {
                resultSource = "evaluation"
                currentScreen = "evaluation"
            },
            onHomeClick       = { currentScreen = "home" },
            onSettingsClick   = { currentScreen = "settings" },
            onCardClick       = { entity ->
                resultSource = "history"
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

        "settings" -> SettingsScreen(
            onHistoryClick = { currentScreen = "history" },
            onStartEvaluationClick = {
                resultSource = "evaluation"
                currentScreen = "evaluation"
            },
            onSettingsClick = { },
            onAboutClick = { currentScreen = "about" },
            onFaqClick = { currentScreen = "faq" },
            onEditProfileClick = { currentScreen = "edit_profile" },
        )

        "about" -> AboutScreen(
            onBackClick = { currentScreen = "settings" }
        )

        "faq" -> FaqScreen(
            onBackClick = { currentScreen = "settings" }
        )

        "edit_profile" -> EditProfileScreen(
            onBackClick = { currentScreen = "settings" }
        )

        else -> currentScreen = "home"
    }
}