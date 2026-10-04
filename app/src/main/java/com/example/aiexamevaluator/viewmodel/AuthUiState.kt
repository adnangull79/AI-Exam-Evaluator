package com.example.aiexamevaluator.viewmodel

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    // Forgot password / reset email state
    val isResetLoading: Boolean = false,
    val resetEmailSent: Boolean = false,
    val resetError: String? = null,
)