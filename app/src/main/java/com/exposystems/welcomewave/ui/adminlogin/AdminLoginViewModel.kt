package com.exposystems.welcomewave.ui.adminlogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.AdminLoginUiState // IMPORTANT: Import from data.model
import com.exposystems.welcomewave.data.repository.AuthRepository // Import AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository // Inject AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState = _uiState.asStateFlow()

    // No longer need the hardcoded correctPin as we're using Firebase Auth

    // Handle email input
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, showError = false, errorMessage = null) }
    }

    // Handle password input
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, showError = false, errorMessage = null) }
    }

    // Modified login function to use Firebase Authentication
    fun onLoginClicked(onLoginSuccess: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(showError = true, errorMessage = "Email and password cannot be empty.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showError = false, errorMessage = null) }
            val user = authRepository.loginUser(email, password)
            if (user != null) {
                // Login successful
                _uiState.update { it.copy(isLoading = false, showError = false, errorMessage = null) }
                onLoginSuccess()
            } else {
                // Login failed - AuthRepository logs the error to Logcat
                _uiState.update { it.copy(isLoading = false, showError = true, errorMessage = "Invalid email or password.") }
            }
        }
    }

    // Function to check if a user is already logged in (for auto-login/session management)
    // Will be used later, e.g., in MainActivity or WelcomeViewModel if you want auto-login
    fun checkIfUserIsLoggedIn(): Boolean {
        return authRepository.currentUser != null
    }

    // Optional: Function to allow admin registration (can be temporary for initial setup)
    suspend fun registerAdmin(email: String, password: String): Boolean {
        return authRepository.registerUser(email, password) != null
    }
}