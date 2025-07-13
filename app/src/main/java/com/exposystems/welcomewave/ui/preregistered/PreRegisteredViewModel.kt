package com.exposystems.welcomewave.ui.preregistered

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.CheckInRequest
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class PreRegisteredUiState(
    val isLoading: Boolean = false,
    val guests: List<PreRegisteredGuest> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PreRegisteredViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val visitorLogRepository: VisitorLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreRegisteredUiState())
    val uiState = _uiState.asStateFlow()
    private val preregistrationCollection = Firebase.firestore.collection("preregistrations")

    init {
        fetchPendingGuests()
    }

    private fun fetchPendingGuests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val snapshot = preregistrationCollection
                    .whereEqualTo("status", "pending")
                    .orderBy("arrivalTimestamp")
                    .get()
                    .await()

                val guestList = snapshot.documents.map { doc ->
                    PreRegisteredGuest(
                        id = doc.id,
                        visitorName = doc.getString("visitorName") ?: "Unknown Visitor",
                        visitorCompany = doc.getString("visitorCompany"),
                        employeeToSee = doc.getString("employeeToSee") ?: "Unknown Employee"
                    )
                }
                _uiState.update { it.copy(isLoading = false, guests = guestList) }

            } catch (e: Exception) {
                Log.e("PreRegViewModel", "Error fetching guests", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun checkInGuest(guest: PreRegisteredGuest) {
        viewModelScope.launch {
            try {
                val employee = employeeRepository.getEmployeeById(guest.employeeToSee)
                if (employee == null) {
                    _uiState.update { it.copy(error = "Could not find employee details.") }
                    return@launch
                }

                val request = CheckInRequest(
                    employeeEmail = employee.email,
                    visitorCompany = guest.visitorCompany ?: "",
                    visitorNames = listOf(guest.visitorName)
                )

                val success = visitorLogRepository.logCheckIn(
                    checkInRequest = request,
                    employeeFirestoreId = employee.id,
                    employeeName = "${employee.firstName} ${employee.lastName}"
                )

                if (success) {
                    preregistrationCollection.document(guest.id).update("status", "checkedIn").await()
                    fetchPendingGuests()
                } else {
                    _uiState.update { it.copy(error = "Failed to send notification.") }
                }

            } catch (e: Exception) {
                Log.e("PreRegViewModel", "Error checking in guest", e)
                _uiState.update { it.copy(error = "Failed to check in guest: ${e.message}") }
            }
        }
    }
}