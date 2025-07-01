package com.exposystems.welcomewave.ui.admin

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.Employee
import com.exposystems.welcomewave.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// This class holds the state for our form fields
data class AddEditUiState(
    val name: String = "",
    val title: String = "",
    val email: String = "",
    val photoUri: String? = null
)

@HiltViewModel
class AdminAddEditViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddEditUiState())
        private set

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name)
    }

    fun onTitleChange(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPhotoSelected(uri: Uri?) {
        // We store the URI as a String to save it in the database
        uiState = uiState.copy(photoUri = uri?.toString())
    }

    fun saveEmployee() {
        if (uiState.name.isBlank() || uiState.title.isBlank() || uiState.email.isBlank()) {
            return // Simple validation
        }

        viewModelScope.launch {
            repository.addEmployee(
                Employee(
                    name = uiState.name.trim(),
                    title = uiState.title.trim(),
                    email = uiState.email.trim(),
                    photoUri = uiState.photoUri
                )
            )
        }
    }
}