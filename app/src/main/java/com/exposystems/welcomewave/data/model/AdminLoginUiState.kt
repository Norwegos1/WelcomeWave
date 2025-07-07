package com.exposystems.welcomewave.data.model

data class AdminLoginUiState(
    val email: String = "",
    val password: String = "",
    val showError: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)