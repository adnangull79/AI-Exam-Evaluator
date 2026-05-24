package com.example.aiexamevaluator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiexamevaluator.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isDataLoaded = false,
                    isUpdating = false,
                    error = null,
                )
            }

            repository.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isDataLoaded = true,
                            isUpdating = false,
                            fullName = user.fullName,
                            email = user.email,
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isDataLoaded = false,
                            isUpdating = false,
                            error = throwable.message,
                        )
                    }
                }
        }
    }

    fun updateFullName(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            _uiState.update {
                it.copy(error = "Full name cannot be empty.", updateSuccess = false)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    updateSuccess = false,
                    error = null,
                )
            }

            repository.updateFullName(trimmedName)
                .onSuccess {
                    loadUser()
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = true,
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = false,
                            error = throwable.message,
                        )
                    }
                }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    updateSuccess = false,
                    error = null,
                )
            }

            repository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = true,
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = false,
                            error = throwable.message,
                        )
                    }
                }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update {
            it.copy(
                isLoading = true,
                isDataLoaded = false,
                isUpdating = false,
                updateSuccess = false,
                fullName = "",
                email = "",
                error = null,
            )
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    updateSuccess = false,
                    error = null,
                )
            }

            repository.deleteAccount()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = true,
                            isLoading = true,
                            isDataLoaded = false,
                            fullName = "",
                            email = "",
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = false,
                            error = throwable.message,
                        )
                    }
                }
        }
    }

    fun clearUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
}
