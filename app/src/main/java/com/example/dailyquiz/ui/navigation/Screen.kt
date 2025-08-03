package com.example.dailyquiz.ui.navigation

sealed class Screen(val route: String) {
    object Quiz : Screen("quiz_screen")
    object History : Screen("history_screen")
    object Details : Screen("details_screen/{attemptId}") {
        fun createRoute(attemptId: Int) = "details_screen/$attemptId"
    }
}