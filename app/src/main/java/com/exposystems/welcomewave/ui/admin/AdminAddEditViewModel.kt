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
 * Represents the state of the Add/Edit Employee screen. It holds all the properties
 * that the UI needs to display, such as input field values and loading/error states.
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

/**
 * The ViewModel for the [AdminAddEditEmployeeScreen].
 * It is responsible for holding the screen's state, handling user input,
 * and communicating with the [EmployeeRepository] to save or update data.
 */
@HiltViewModel
class AdminAddEditViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    savedStateHandle: SavedStateHandle, // Used to retrieve navigation arguments like the employee ID.
    @Suppress("unused") @ApplicationContext private val application: Context
) : ViewModel() {

    /**
     * The single source of truth for the UI. The @Composable screen observes this
     * state and recomposes whenever it changes.
     */
    var uiState by mutableStateOf(AddEditUiState())
        private set // The state can only be modified within this ViewModel.

    // Holds the ID of the employee being edited. It's null if we are adding a new employee.
    private var existingEmployeeId: String? = null

    init {
        // When the ViewModel is created, get the employeeId passed from the previous screen.
        existingEmployeeId = savedStateHandle["employeeId"]

        // Check if we are editing an existing employee or adding a new one.
        if (existingEmployeeId != null && existingEmployeeId != "-1") {
            // If an ID is present, we are in "edit mode".
            uiState = uiState.copy(isNewEmployee = false)
            loadEmployee(existingEmployeeId!!)
        } else {
            // If no ID is present, we are in "add mode".
            uiState = uiState.copy(isNewEmployee = true)
        }
    }

    /**
     * Fetches the details of a specific employee from the repository and updates
     * the UI state to populate the form fields for editing.
     * @param id The Firestore document ID of the employee to load.
     */
    private fun loadEmployee(id: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, showError = false)
            try {
                employeeRepository.getEmployeeById(id)?.let { employee ->
                    // If the employee is found, update the state with their details.
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
                    // If no employee is found with that ID, show an error.
                    uiState = uiState.copy(
                        showError = true,
                        errorMessage = "Employee not found.",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Handle any exceptions during the data fetching process.
                uiState = uiState.copy(
                    showError = true,
                    errorMessage = "Error loading employee: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // --- The following functions handle user input from the UI ---

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
     * Handles the URI of a photo selected from the device's gallery.
     * This function currently saves the URI as a string. A full implementation
     * would involve uploading the image file to Firebase Storage and then
     * saving the resulting download URL.
     * @param uri The content URI of the selected photo.
     */
    fun onPhotoSelected(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            // Convert the content URI to a persistent string path for the state.
            val photoPath = uri.toString()
            uiState = uiState.copy(photoUrl = photoPath)
        }
    }

    /**
     * Validates user input and saves a new or updated employee to the repository.
     * @param onNavigateUp Callback to navigate back to the previous screen on successful save.
     */
    fun onSaveEmployeeClicked(onNavigateUp: () -> Unit) {
        // --- Validation Step ---
        if (uiState.firstName.isBlank() || uiState.lastName.isBlank() || uiState.email.isBlank()) {
            uiState = uiState.copy(
                showError = true,
                errorMessage = "First Name, Last Name, and Email are required."
            )
            return // Stop the function if validation fails.
        }
        // Basic email format validation using Android's built-in email pattern.
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            uiState =
                uiState.copy(showError = true, errorMessage = "Please enter a valid email address.")
            return
        }

        // --- Save Operation ---
        uiState = uiState.copy(isLoading = true, showError = false, errorMessage = null)

        viewModelScope.launch {
            try {
                // Decide whether to add a new employee or update an existing one.
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
                        id = existingEmployeeId!!, // Use the ID of the employee we are editing.
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
                // If the save is successful, stop loading and navigate up.
                uiState = uiState.copy(isLoading = false)
                onNavigateUp()
            } catch (e: Exception) {
                // If an error occurs during the save, show an error message.
                uiState = uiState.copy(
                    isLoading = false,
                    showError = true,
                    errorMessage = "Failed to save employee: ${e.message}"
                )
            }
        }
    }
}