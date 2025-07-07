package com.exposystems.welcomewave.ui.admin

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee // Ensure this imports your NEW Employee data class
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

data class AddEditUiState(
    val firstName: String = "", // Changed from 'name'
    val lastName: String = "",  // Added
    val title: String = "",
    val email: String = "",
    val photoUrl: String? = null // Changed from photoUri
)

@HiltViewModel
class AdminAddEditViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository, // Renamed 'repository' for clarity
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val application: Context
) : ViewModel() {

    var uiState by mutableStateOf(AddEditUiState())
        private set

    // Changed from Int? to String? for Firestore IDs
    private var existingEmployeeId: String? = null

    init {
        // We get the employeeId from the handle here...
        // Retrieve as String, not Int
        existingEmployeeId = savedStateHandle["employeeId"]
        if (existingEmployeeId != null && existingEmployeeId != "-1") { // Check against string for placeholder
            loadEmployee(existingEmployeeId!!)
        }
    }

    private fun loadEmployee(id: String) { // Changed ID type to String
        viewModelScope.launch {
            employeeRepository.getEmployeeById(id)?.let { employee -> // Changed to getEmployeeById
                uiState = uiState.copy(
                    firstName = employee.firstName, // Use new fields
                    lastName = employee.lastName,   // Use new fields
                    title = employee.title ?: "",   // Handle nullable title
                    email = employee.email,
                    photoUrl = employee.photoUrl    // Use new field
                )
            }
        }
    }

    fun onFirstNameChange(name: String) { // Renamed from onNameChange
        uiState = uiState.copy(firstName = name)
    }

    fun onLastNameChange(name: String) { // Added for lastName
        uiState = uiState.copy(lastName = name)
    }

    fun onTitleChange(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPhotoSelected(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            val fileName = "employee_${UUID.randomUUID()}.jpg"
            val inputStream = application.contentResolver.openInputStream(uri)
            val file = File(application.filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            // Store local file path for now; upload to Firebase Storage later
            uiState = uiState.copy(photoUrl = file.absolutePath)
        }
    }

    fun saveEmployee() {
        // Validate required fields (using firstName for name validation)
        if (uiState.firstName.isBlank() || uiState.lastName.isBlank() || uiState.email.isBlank()) return

        viewModelScope.launch {
            if (existingEmployeeId == null || existingEmployeeId == "-1") { // Check against string for placeholder
                // Logic for a NEW employee
                val newEmployee = Employee(
                    // Firestore will auto-generate 'id' if left as default empty string
                    firstName = uiState.firstName.trim(),
                    lastName = uiState.lastName.trim(),
                    email = uiState.email.trim(),
                    title = uiState.title.trim().takeIf { it.isNotBlank() }, // Ensure empty string becomes null
                    photoUrl = uiState.photoUrl
                    // createdAt and updatedAt will be set by @ServerTimestamp
                )
                employeeRepository.addEmployee(newEmployee)
            } else {
                // Logic for an EXISTING employee
                val updatedEmployee = Employee(
                    id = existingEmployeeId!!, // Use the existing Firestore String ID
                    firstName = uiState.firstName.trim(),
                    lastName = uiState.lastName.trim(),
                    email = uiState.email.trim(),
                    title = uiState.title.trim().takeIf { it.isNotBlank() }, // Ensure empty string becomes null
                    photoUrl = uiState.photoUrl
                    // updatedAt will be set by @ServerTimestamp
                )
                employeeRepository.updateEmployee(updatedEmployee)
            }
        }
    }
}