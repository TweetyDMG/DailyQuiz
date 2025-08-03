package com.example.dailyquiz.data.source.remote

import com.example.dailyquiz.data.model.QuizResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 5,
        @Query("type") type: String = "multiple"
    ): QuizResponse
}