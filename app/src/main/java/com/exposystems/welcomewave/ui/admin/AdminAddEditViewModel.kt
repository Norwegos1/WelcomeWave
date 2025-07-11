package com.exposystems.welcomewave.ui.admin

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Add/Edit Employee screen.
 */
data class AddEditUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val title: String = "",
    val department: String = "",
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val errorMessage: String? = null,
    val isNewEmployee: Boolean = false
)

@HiltViewModel
class AdminAddEditViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    savedStateHandle: SavedStateHandle,
    @Suppress("unused") @ApplicationContext private val application: Context
) : ViewModel() {

    var uiState by mutableStateOf(AddEditUiState())
        private set

    private var existingEmployeeId: String? = null

    init {
        existingEmployeeId = savedStateHandle["employeeId"]
        if (existingEmployeeId != null && existingEmployeeId != "-1") {
            // Logic for editing an existing employee
            uiState = uiState.copy(isNewEmployee = false)
            loadEmployee(existingEmployeeId!!)
        } else {
            // Logic for adding a new employee
            uiState = uiState.copy(isNewEmployee = true)
        }
    }

    private fun loadEmployee(id: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, showError = false)
            try {
                employeeRepository.getEmployeeById(id)?.let { employee ->
                    uiState = uiState.copy(
                        firstName = employee.firstName,
                        lastName = employee.lastName,
                        email = employee.email,
                        title = employee.title ?: "",
                        department = employee.department ?: "",
                        photoUrl = employee.photoUrl,
                        isActive = employee.isactive,
                        isLoading = false
                    )
                } ?: run {
                    uiState = uiState.copy(showError = true, errorMessage = "Employee not found.", isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(showError = true, errorMessage = "Error loading employee: ${e.message}", isLoading = false)
            }
        }
    }

    fun onFirstNameChange(name: String) {
        uiState = uiState.copy(firstName = name, showError = false, errorMessage = null)
    }

    fun onLastNameChange(name: String) {
        uiState = uiState.copy(lastName = name, showError = false, errorMessage = null)
    }

    fun onTitleChange(title: String) {
        uiState = uiState.copy(title = title, showError = false, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, showError = false, errorMessage = null)
    }

    fun onDepartmentChange(department: String) {
        uiState = uiState.copy(department = department, showError = false, errorMessage = null)
    }

    fun onIsActiveChange(active: Boolean) {
        uiState = uiState.copy(isActive = active)
    }

    /**
     * Handles selection of a photo URI (e.g., from gallery).
     * In a real app, this would trigger an upload to Firebase Storage.
     */
    fun onPhotoSelected(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            val photoPath = uri.toString()
            uiState = uiState.copy(photoUrl = photoPath)
        }
    }

    /**
     * Handles saving a new or updated employee.
     * @param onNavigateUp Callback to navigate back on successful save.
     */
    fun onSaveEmployeeClicked(onNavigateUp: () -> Unit) {
        // Basic validation for required fields
        if (uiState.firstName.isBlank() || uiState.lastName.isBlank() || uiState.email.isBlank()) {
            uiState = uiState.copy(showError = true, errorMessage = "First Name, Last Name, and Email are required.")
            return
        }
        // Basic email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            uiState = uiState.copy(showError = true, errorMessage = "Please enter a valid email address.")
            return
        }

        uiState = uiState.copy(isLoading = true, showError = false, errorMessage = null)

        viewModelScope.launch {
            try {
                if (uiState.isNewEmployee) {
                    val newEmployee = Employee(
                        firstName = uiState.firstName.trim(),
                        lastName = uiState.lastName.trim(),
                        email = uiState.email.trim(),
                        title = uiState.title.trim().takeIf { it.isNotBlank() },
                        department = uiState.department.trim().takeIf { it.isNotBlank() },
                        photoUrl = uiState.photoUrl,
                        isactive = uiState.isActive
                    )
                    employeeRepository.addEmployee(newEmployee)
                } else {
                    val updatedEmployee = Employee(
                        id = existingEmployeeId!!,
                        firstName = uiState.firstName.trim(),
                        lastName = uiState.lastName.trim(),
                        email = uiState.email.trim(),
                        title = uiState.title.trim().takeIf { it.isNotBlank() },
                        department = uiState.department.trim().takeIf { it.isNotBlank() },
                        photoUrl = uiState.photoUrl,
                        isactive = uiState.isActive
                    )
                    employeeRepository.updateEmployee(updatedEmployee)
                }
                uiState = uiState.copy(isLoading = false)
                onNavigateUp() // Navigate back on success
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, showError = true, errorMessage = "Failed to save employee: ${e.message}")
            }
        }
    }
}