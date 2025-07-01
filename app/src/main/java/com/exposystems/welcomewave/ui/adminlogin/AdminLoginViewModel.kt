package com.exposystems.welcomewave.ui.adminlogin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AdminLoginUiState(
    val enteredPin: String = "",
    val showError: Boolean = false
)

@HiltViewModel
class AdminLoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState = _uiState.asStateFlow()

    private val correctPin = "1234" // Hardcoded correct PIN

    fun onPinChange(pin: String) {
        if (pin.length <= 4) {
            _uiState.update { it.copy(enteredPin = pin, showError = false) }
        }
    }

    fun onLoginClicked(onLoginSuccess: () -> Unit) {
        if (_uiState.value.enteredPin == correctPin) {
            onLoginSuccess()
        } else {
            _uiState.update { it.copy(showError = true) }
        }
    }
}