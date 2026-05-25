package com.example.aiexamevaluator.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiexamevaluator.data.local.EvaluationHistoryEntity
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.repository.EvaluationRepository
import com.example.aiexamevaluator.repository.HistoryRepository
import com.example.aiexamevaluator.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationUiState())
    val uiState: StateFlow<EvaluationUiState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<EvaluationHistoryEntity>> = historyRepository
        .getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

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
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            historyRepository.saveEvaluation(
                                studentName = name,
                                rollNumber = roll,
                                totalMarks = totalMarks,
                                response = response,
                            )
                        } catch (_: Exception) {
                            // Ignore history write failures so evaluation result still reaches the UI.
                        }
                    }
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

    fun deleteHistory(entity: EvaluationHistoryEntity) {
        viewModelScope.launch {
            try {
                historyRepository.deleteEvaluation(entity)
            } catch (_: Exception) {
                // Handle safely.
            }
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            try {
                historyRepository.deleteAllHistory()
            } catch (_: Exception) {
                // Safe fail
            }
        }
    }

    private fun resolveApplicationContext(): Context? {
        return runCatching {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
            currentApplicationMethod.invoke(null) as? Context
        }.getOrNull()
    }
}
