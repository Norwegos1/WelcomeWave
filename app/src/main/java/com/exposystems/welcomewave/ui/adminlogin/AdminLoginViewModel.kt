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

    // --- NEW: State for logout result ---
    private val _logoutSuccessful = MutableStateFlow<Boolean?>(null) // Null means no logout attempt yet, true/false for result
    val logoutSuccessful: StateFlow<Boolean?> = _logoutSuccessful.asStateFlow()
    // --- END NEW ---

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
                _isLoggedIn.value = true // Indicate successful login to trigger navigation
            } else {
                _uiState.update { it.copy(isLoading = false, showError = true, errorMessage = "Invalid email or password.") }
            }
        }
    }

    // --- NEW FUNCTION: Logout ---
    fun logout() {
        viewModelScope.launch {
            val result = authRepository.signOut()
            _logoutSuccessful.value = result // Update the state with true/false based on logout success
            if (result) { // If logout was successful
                _isLoggedIn.value = false // Update login state
                _uiState.update { it.copy(email = "", password = "") } // Clear login fields for next login
            }
            // No need to show error on UI for logout usually, just log it.
        }
    }

    // NEW: Function to clear the logout state after UI consumes it
    fun clearLogoutState() {
        _logoutSuccessful.value = null
    }


}