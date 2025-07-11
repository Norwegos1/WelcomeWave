package com.exposystems.welcomewave.ui.adminlogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.AdminLoginUiState
import com.exposystems.welcomewave.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _logoutSuccessful = MutableStateFlow<Boolean?>(null)
    val logoutSuccessful: StateFlow<Boolean?> = _logoutSuccessful.asStateFlow()

    // NEW: State for Forgot Password result
    private val _forgotPasswordResult = MutableStateFlow<Boolean?>(null) // True for success, False for failure, null for no attempt
    val forgotPasswordResult: StateFlow<Boolean?> = _forgotPasswordResult.asStateFlow()
    // END NEW

    init {
        checkInitialLoginState()
    }

    private fun checkInitialLoginState() {
        _isLoggedIn.value = (authRepository.currentUser != null)
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, showError = false, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, showError = false, errorMessage = null) }
    }

    fun onLoginClicked() {
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
                _uiState.update { it.copy(isLoading = false, showError = false, errorMessage = null) }
                _isLoggedIn.value = true
            } else {
                _uiState.update { it.copy(isLoading = false, showError = true, errorMessage = "Invalid email or password.") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = authRepository.signOut()
            _logoutSuccessful.value = result
            if (result) {
                _isLoggedIn.value = false
                _uiState.update { it.copy(email = "", password = "") }
            }
        }
    }

    fun clearLogoutState() {
        _logoutSuccessful.value = null
    }

    // NEW: Function to send password reset email
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            // Optional: Handle empty email for password reset
            return
        }
        viewModelScope.launch {
            _forgotPasswordResult.value = null // Clear previous result
            val success = authRepository.sendPasswordResetEmail(email) // Assumes AuthRepository has this function
            _forgotPasswordResult.value = success
        }
    }

    // NEW: Function to clear Forgot Password result state
    fun clearForgotPasswordResult() {
        _forgotPasswordResult.value = null
    }
}