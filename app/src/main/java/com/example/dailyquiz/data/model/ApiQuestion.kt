package com.example.dailyquiz.data.model

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val results: List<ApiQuestion>
)

data class ApiQuestion(
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("category") val category: String,
    @SerializedName("question") val questionText: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)