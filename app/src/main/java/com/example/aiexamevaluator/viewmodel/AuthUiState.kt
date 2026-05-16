package com.example.aiexamevaluator.viewmodel

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
)
