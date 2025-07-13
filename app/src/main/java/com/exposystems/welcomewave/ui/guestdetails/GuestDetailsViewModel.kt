package com.exposystems.welcomewave.ui.guestdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.CheckInRequest
import com.exposystems.welcomewave.data.model.Employee
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class Guest(
    val id: UUID = UUID.randomUUID(),
    val name: String = ""
)

data class GuestDetailsUiState(
    val selectedEmployee: Employee? = null,
    val companyName: String = "",
    val guests: List<Guest> = listOf(Guest()),
    val isCheckInEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null // UPDATED: To hold error messages
)

@HiltViewModel
class GuestDetailsViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val visitorLogRepository: VisitorLogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val employeeId: String? = savedStateHandle["employeeId"]
        if (employeeId != null && employeeId != "-1") {
            loadEmployeeDetails(employeeId)
        }
    }

    private fun loadEmployeeDetails(employeeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val employee = employeeRepository.getEmployeeById(employeeId)
            _uiState.update { it.copy(selectedEmployee = employee, isLoading = false) }
            validateCheckIn()
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

    fun checkInGuests(onCheckInComplete: () -> Unit) {
        if (!uiState.value.isCheckInEnabled || uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            uiState.value.selectedEmployee?.let { employee ->
                val request = CheckInRequest(
                    employeeEmail = employee.email,
                    visitorCompany = uiState.value.companyName,
                    visitorNames = uiState.value.guests.map { it.name }
                )

                val success = visitorLogRepository.logCheckIn(
                    checkInRequest = request,
                    employeeFirestoreId = employee.id,
                    employeeName = "${employee.firstName} ${employee.lastName}"
                )

                if (success) {
                    onCheckInComplete()
                } else {
                    _uiState.update { it.copy(errorMessage = "Check-in failed. Please check your network connection and try again.") }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun validateCheckIn() {
        val state = _uiState.value
        val isReady = state.companyName.isNotBlank() &&
                state.guests.all { it.name.isNotBlank() } &&
                state.selectedEmployee != null
        _uiState.update { it.copy(isCheckInEnabled = isReady) }
    }
}