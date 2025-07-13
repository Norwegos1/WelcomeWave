package com.exposystems.welcomewave.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class WelcomeUiState(
    val greeting: String = "Welcome"
)

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState = _uiState.asStateFlow()
    private var adminTapCount = 0
    private var resetAdminTapJob: Job? = null

    init {
        updateGreeting()
    }

    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val newGreeting = when (hour) {
            in 0..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
        _uiState.update { it.copy(greeting = newGreeting) }
    }

    fun onAdminGestureTapped(onAdminNavigate: () -> Unit) {
        resetAdminTapJob?.cancel() // Cancel any previous reset job

        adminTapCount++
        if (adminTapCount >= 5) {
            adminTapCount = 0
            onAdminNavigate()
        } else {
            // Launch a job to reset the tap count after 2 seconds
            resetAdminTapJob = viewModelScope.launch {
                delay(2000)
                adminTapCount = 0
            }
        }
    }
}