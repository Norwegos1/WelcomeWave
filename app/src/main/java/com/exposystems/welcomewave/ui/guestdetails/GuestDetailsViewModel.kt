package com.exposystems.welcomewave.ui.guestdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.Employee
import com.exposystems.welcomewave.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// A data class to represent a single guest, with a unique ID for list management.
data class Guest(
    val id: UUID = UUID.randomUUID(),
    val name: String = ""
)

data class GuestDetailsUiState(
    val selectedEmployee: Employee? = null,
    val companyName: String = "",
    val guests: List<Guest> = listOf(Guest()), // Start with one empty guest
    val isCheckInEnabled: Boolean = false
)

@HiltViewModel
class GuestDetailsViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    savedStateHandle: SavedStateHandle // Used to get navigation arguments
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Retrieve the employeeId passed through navigation
        val employeeId: Int? = savedStateHandle["employeeId"]
        if (employeeId != null && employeeId != -1) {
            loadEmployeeDetails(employeeId)
        }
    }

    private fun loadEmployeeDetails(employeeId: Int) {
        viewModelScope.launch {
            // Efficiently fetch only the selected employee from the database
            val employee = employeeRepository.getEmployee(employeeId)
            _uiState.update { it.copy(selectedEmployee = employee) }
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
                currentState
            }
        }
        validateCheckIn()
    }

    private fun validateCheckIn() {
        val state = _uiState.value
        // The check-in button is enabled only when all fields are valid
        val isReady = state.companyName.isNotBlank() &&
                state.guests.all { it.name.isNotBlank() } &&
                state.selectedEmployee != null
        _uiState.update { it.copy(isCheckInEnabled = isReady) }
    }
}