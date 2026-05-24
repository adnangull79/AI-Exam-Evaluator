package com.example.aiexamevaluator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiexamevaluator.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoginState()
    }

    fun signUp(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()

        when {
            trimmedName.isBlank() || trimmedEmail.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _uiState.update { it.copy(error = "Please fill in all fields.", isLoading = false, isSuccess = false) }
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                _uiState.update { it.copy(error = "Please enter a valid email address.", isLoading = false, isSuccess = false) }
                return
            }

            password.length < 8 -> {
                _uiState.update { it.copy(error = "Password must be at least 8 characters.", isLoading = false, isSuccess = false) }
                return
            }

            password != confirmPassword -> {
                _uiState.update { it.copy(error = "Passwords do not match.", isLoading = false, isSuccess = false) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            try {
                repository.signUp(trimmedName, trimmedEmail, password)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true, error = null) }
                    }
                    .onFailure { throwable ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                error = throwable.message ?: "Sign up failed. Please try again.",
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = e.message ?: "Unexpected error. Please try again.",
                    )
                }
            }
        }
    }

    fun login(
        email: String,
        password: String,
    ) {
        val trimmedEmail = email.trim()

        when {
            trimmedEmail.isBlank() || password.isBlank() -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        isLoggedIn = false,
                        error = "Please enter both email and password.",
                    )
                }
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        isLoggedIn = false,
                        error = "Please enter a valid email address.",
                    )
                }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSuccess = false, isLoggedIn = false, error = null) }

            try {
                repository.login(trimmedEmail, password)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isLoggedIn = true,
                                error = null,
                            )
                        }
                    }
                    .onFailure { throwable ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = false,
                                isLoggedIn = false,
                                error = throwable.message ?: "Login failed. Please try again.",
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        isLoggedIn = false,
                        error = e.message ?: "Unexpected error. Please try again.",
                    )
                }
            }
        }
    }

    private fun checkLoginState() {
        if (repository.isUserLoggedIn()) {
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState(
            isLoading = false,
            isSuccess = false,
            isLoggedIn = false,
            error = null,
        )
    }

    fun refreshLoginState() {
        _uiState.update {
            it.copy(
                isLoggedIn = repository.isUserLoggedIn(),
            )
        }
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
