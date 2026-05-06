package com.example.aiexamevaluator.repository

import com.example.aiexamevaluator.data.local.EvaluationDao
import com.example.aiexamevaluator.data.local.EvaluationHistoryEntity
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class HistoryRepository(
    private val dao: EvaluationDao,
) {

    fun getAllHistory(): Flow<List<EvaluationHistoryEntity>> {
        return dao.getAllEvaluations().flowOn(Dispatchers.IO)
    }

    suspend fun saveEvaluation(
        studentName: String,
        rollNumber: String,
        totalMarks: Int,
        response: EvaluationResponse,
    ) {
        withContext(Dispatchers.IO) {
            val entity = EvaluationHistoryEntity(
                studentName = studentName,
                rollNumber = rollNumber,
                totalMarks = totalMarks,
                totalObtained = response.totalObtained,
                confidenceScore = response.confidenceScore,
                overallFeedback = response.overallFeedback,
                questionsJson = Gson().toJson(response.questions),
                evaluatedAt = System.currentTimeMillis(),
            )
            dao.insertEvaluation(entity)
        }
    }

    suspend fun deleteEvaluation(entity: EvaluationHistoryEntity) {
        withContext(Dispatchers.IO) {
            dao.deleteEvaluation(entity)
        }
    }
}