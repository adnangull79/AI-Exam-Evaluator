package com.example.aiexamevaluator.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aiexamevaluator.data.local.AppDatabase
import com.example.aiexamevaluator.repository.EvaluationRepository
import com.example.aiexamevaluator.repository.HistoryRepository

class EvaluationViewModelFactory(
    context: Context,
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EvaluationViewModel::class.java)) {
            val database = AppDatabase.getInstance(appContext)
            val historyRepository = HistoryRepository(database.evaluationDao())
            @Suppress("UNCHECKED_CAST")
            return EvaluationViewModel(
                repository = EvaluationRepository(),
                historyRepository = historyRepository,
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}