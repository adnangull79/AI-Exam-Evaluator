package com.example.aiexamevaluator.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.repository.EvaluationRepository
import com.example.aiexamevaluator.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EvaluationUiState(
    val isLoading: Boolean = false,
    val result: EvaluationResponse? = null,
    val error: String? = null,
    val studentName: String = "",
    val rollNumber: String = "",
    val totalMarks: Int = 0,
)

class EvaluationViewModel(
    private val repository: EvaluationRepository = EvaluationRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationUiState())
    val uiState: StateFlow<EvaluationUiState> = _uiState.asStateFlow()

    fun evaluateExam(
        questionImages: List<Uri>,
        answerImages: List<Uri>,
        name: String,
        roll: String,
        totalMarks: Int,
        isMcq: Boolean,
    ) {
        if (name.isBlank() || roll.isBlank() || totalMarks <= 0 || answerImages.isEmpty()) {
            _uiState.update { it.copy(error = "Please complete all required fields.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    result = null,
                    studentName = name,
                    rollNumber = roll,
                    totalMarks = totalMarks,
                )
            }

            val context = resolveApplicationContext()
            if (context == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        result = null,
                        error = "Unable to access application context.",
                    )
                }
                return@launch
            }

            val encodedQuestionImages = ImageUtils.encodeUrisToBase64(context, questionImages)
            val encodedAnswerImages = ImageUtils.encodeUrisToBase64(context, answerImages)

            val evaluationResult = repository.evaluateExam(
                questionImages = encodedQuestionImages,
                answerImages = encodedAnswerImages,
                name = name,
                roll = roll,
                totalMarks = totalMarks,
                isMcq = isMcq,
            )

            evaluationResult
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            result = response,
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            result = null,
                            error = throwable.message ?: "Evaluation failed. Please try again.",
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearResult() {
        _uiState.update { it.copy(result = null) }
    }

    private fun resolveApplicationContext(): Context? {
        return runCatching {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
            currentApplicationMethod.invoke(null) as? Context
        }.getOrNull()
    }
}
