package com.example.dailyquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dailyquiz.ui.screens.details.DetailsScreen
import com.example.dailyquiz.ui.screens.history.HistoryScreen
import com.example.dailyquiz.ui.screens.quiz.QuizScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Quiz.route) {
        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = hiltViewModel(),
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = hiltViewModel(),
                onNavigateToDetails = { attemptId ->
                    navController.navigate(Screen.Details.createRoute(attemptId))
                }
            )
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("attemptId") { type = NavType.IntType })
        ) {
            DetailsScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}