package com.example.aiexamevaluator.viewmodel

data class UserUiState(
    val isLoading: Boolean = true,
    val isDataLoaded: Boolean = false,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val fullName: String = "",
    val email: String = "",
    val error: String? = null
)