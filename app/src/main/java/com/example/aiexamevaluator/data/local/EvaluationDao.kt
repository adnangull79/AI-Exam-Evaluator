package com.example.aiexamevaluator.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(entity: EvaluationHistoryEntity)

    @Query("SELECT * FROM evaluation_history ORDER BY evaluatedAt DESC")
    fun getAllEvaluations(): Flow<List<EvaluationHistoryEntity>>

    @Delete
    suspend fun deleteEvaluation(entity: EvaluationHistoryEntity)

    @Query("DELETE FROM evaluation_history")
    suspend fun deleteAll()
}