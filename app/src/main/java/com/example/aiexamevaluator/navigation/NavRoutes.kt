package com.example.aiexamevaluator.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Evaluation : NavRoutes("evaluation")
    data object Result : NavRoutes("result")
}
