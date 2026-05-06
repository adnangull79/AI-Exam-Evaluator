package com.example.aiexamevaluator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluation_history")
data class EvaluationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentName: String,
    val rollNumber: String,
    val totalMarks: Int,
    val totalObtained: Int,
    val confidenceScore: Int,
    val overallFeedback: String,
    val questionsJson: String,
    val evaluatedAt: Long,
)