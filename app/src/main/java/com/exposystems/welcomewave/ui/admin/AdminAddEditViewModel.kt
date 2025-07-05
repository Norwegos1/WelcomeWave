package com.exposystems.welcomewave.ui.admin

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.Employee
import com.exposystems.welcomewave.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

data class AddEditUiState(
    val name: String = "",
    val title: String = "",
    val email: String = "",
    val photoUri: String? = null
)

@HiltViewModel
class AdminAddEditViewModel @Inject constructor(
    private val repository: EmployeeRepository,
    savedStateHandle: SavedStateHandle, // "private val" removed
    @ApplicationContext private val application: Context
) : ViewModel() {

    var uiState by mutableStateOf(AddEditUiState())
        private set

    private var existingEmployeeId: Int? = null

    init {
        // We get the employeeId from the handle here...
        existingEmployeeId = savedStateHandle["employeeId"]
        if (existingEmployeeId != null && existingEmployeeId != -1) {
            loadEmployee(existingEmployeeId!!)
        }
    }
    // ...and since we don't use savedStateHandle anywhere else, it doesn't need to be a property.

    private fun loadEmployee(id: Int) {
        viewModelScope.launch {
            repository.getEmployee(id)?.let { employee ->
                uiState = uiState.copy(
                    name = employee.name,
                    title = employee.title,
                    email = employee.email,
                    photoUri = employee.photoUri
                )
            }
        }
    }

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
            uiState = uiState.copy(photoUri = file.absolutePath)
        }
    }

    fun saveEmployee() {
        if (uiState.name.isBlank() || uiState.title.isBlank()) return

        viewModelScope.launch {
            if (existingEmployeeId == null || existingEmployeeId == -1) {
                // Logic for a NEW employee
                val newEmployee = Employee(
                    // When creating, the ID is the default 0, so Room auto-generates it
                    name = uiState.name.trim(),
                    title = uiState.title.trim(),
                    email = uiState.email.trim(),
                    photoUri = uiState.photoUri
                )
                repository.addEmployee(newEmployee)
            } else {
                // Logic for an EXISTING employee
                val updatedEmployee = Employee(
                    id = existingEmployeeId!!, // Use the real ID when updating
                    name = uiState.name.trim(),
                    title = uiState.title.trim(),
                    email = uiState.email.trim(),
                    photoUri = uiState.photoUri
                )
                repository.updateEmployee(updatedEmployee)
            }
        }
    }
}