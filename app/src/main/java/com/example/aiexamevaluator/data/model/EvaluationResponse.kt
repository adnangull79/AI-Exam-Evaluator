package com.example.aiexamevaluator.data.model

data class EvaluationResponse(
    val totalObtained: Int,
    val confidenceScore: Int,
    val overallFeedback: String,
    val questions: List<Question>,
)

data class Question(
    val questionNumber: Int,
    val maxMarks: Int,
    val marksObtained: Int,
    val correctPoints: List<String>,
    val mistakes: List<String>,
    val suggestion: String,
    val reasoning: String,
)
