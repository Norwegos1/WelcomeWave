package com.exposystems.welcomewave.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel() {

    private val _tapCount = MutableStateFlow(0)
    private var guestNavigationJob: Job? = null

    fun onScreenTapped(onGuestNavigate: () -> Unit, onAdminNavigate: () -> Unit) {
        guestNavigationJob?.cancel() // Cancel any pending navigation

        _tapCount.value++

        if (_tapCount.value >= 5) {
            // Admin action
            _tapCount.value = 0
            onAdminNavigate()
        } else {
            // Guest action: schedule navigation
            guestNavigationJob = viewModelScope.launch {
                delay(300)
                onGuestNavigate()
                _tapCount.value = 0
            }
        }
    }
}