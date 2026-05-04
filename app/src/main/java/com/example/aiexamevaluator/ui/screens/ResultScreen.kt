package com.example.aiexamevaluator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aiexamevaluator.data.model.EvaluationResponse

@Composable
fun ResultScreen(
    studentName: String,
    rollNumber: String,
    totalMarks: Int,
    result: EvaluationResponse,
    onEvaluateAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Evaluation Result")
        Text(text = "Name: $studentName")
        Text(text = "Roll Number: $rollNumber")
        Text(text = "Score: ${result.totalObtained} / $totalMarks")
        Text(text = "Confidence: ${result.confidenceScore}%")
        Text(text = "Feedback: ${result.overallFeedback}")

        Button(onClick = onEvaluateAnother, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Evaluate Another")
        }
    }
}
