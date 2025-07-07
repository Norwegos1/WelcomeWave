package com.exposystems.welcomewave.ui.guestdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.CheckInRequest
import com.exposystems.welcomewave.data.model.Employee // Ensure this imports your NEW Employee data class
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository // NEW: Import VisitorLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// Guest and GuestDetailsUiState data classes remain the same...
data class Guest(
    val id: UUID = UUID.randomUUID(),
    val name: String = ""
)

data class GuestDetailsUiState(
    val selectedEmployee: Employee? = null,
    val companyName: String = "",
    val guests: List<Guest> = listOf(Guest()),
    val isCheckInEnabled: Boolean = false,
    val isLoading: Boolean = false // Added for loading state
)

@HiltViewModel
class GuestDetailsViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val visitorLogRepository: VisitorLogRepository, // NEW: Inject VisitorLogRepository
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Retrieve employeeId as String for Firestore
        val employeeId: String? = savedStateHandle["employeeId"]
        if (employeeId != null && employeeId != "-1") { // Check against String placeholder
            loadEmployeeDetails(employeeId)
        }
    }

    private fun loadEmployeeDetails(employeeId: String) { // Changed ID type to String
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Set loading
            val employee = employeeRepository.getEmployeeById(employeeId) // Changed to getEmployeeById
            _uiState.update { it.copy(selectedEmployee = employee, isLoading = false) } // Update and unset loading
        }
    }

    fun onCompanyChange(name: String) {
        _uiState.update { it.copy(companyName = name) }
        validateCheckIn()
    }

    fun onGuestNameChange(id: UUID, name: String) {
        _uiState.update { currentState ->
            val updatedGuests = currentState.guests.map {
                if (it.id == id) it.copy(name = name) else it
            }
            currentState.copy(guests = updatedGuests)
        }
        validateCheckIn()
    }

    fun onAddGuest() {
        _uiState.update { it.copy(guests = it.guests + Guest()) }
        validateCheckIn()
    }

    fun onRemoveGuest(id: UUID) {
        _uiState.update { currentState ->
            if (currentState.guests.size > 1) {
                currentState.copy(guests = currentState.guests.filterNot { it.id == id })
            } else {
                currentState // Don't remove if only one guest left
            }
        }
        validateCheckIn()
    }

    // This is the new function that calls the VisitorLogRepository
    fun checkInGuests(onCheckInComplete: () -> Unit) {
        if (!uiState.value.isCheckInEnabled || uiState.value.isLoading) return // Prevent multiple clicks

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Set loading
            uiState.value.selectedEmployee?.let { employee ->
                val request = CheckInRequest(
                    employeeEmail = employee.email,
                    visitorCompany = uiState.value.companyName,
                    visitorNames = uiState.value.guests.map { it.name }
                )

                // IMPORTANT: Now call logCheckIn from VisitorLogRepository
                val success = visitorLogRepository.logCheckIn(
                    checkInRequest = request,
                    employeeFirestoreId = employee.id, // Pass Firestore String ID
                    employeeName = "${employee.firstName} ${employee.lastName}" // Pass denormalized name
                )

                if (success) {
                    onCheckInComplete()
                } else {
                    // TODO: Handle error, show a message to the user
                }
            }
            _uiState.update { it.copy(isLoading = false) } // Unset loading
        }
    }

    private fun validateCheckIn() {
        val state = _uiState.value
        val isReady = state.companyName.isNotBlank() &&
                state.guests.all { it.name.isNotBlank() } &&
                state.selectedEmployee != null
        _uiState.update { it.copy(isCheckInEnabled = isReady) }
    }
}