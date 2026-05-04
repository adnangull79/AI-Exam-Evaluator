package com.example.aiexamevaluator.data.remote

import com.example.aiexamevaluator.data.model.GeminiRequest
import com.example.aiexamevaluator.data.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun evaluateExam(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest,
    ): GeminiResponse?
}
