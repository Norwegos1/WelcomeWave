package com.exposystems.welcomewave.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee // Ensure this imports your NEW Employee data class
import com.exposystems.welcomewave.data.model.VisitorLog // Ensure this imports your NEW VisitorLog data class
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository // NEW: Import VisitorLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AdminLogUiState(
    val logs: List<VisitorLog> = emptyList(), // Changed from CheckInLog to VisitorLog
    val employees: Map<String, String> = emptyMap() // Changed from Int to String for Employee ID
)

@HiltViewModel
class AdminVisitorLogViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository, // Renamed 'repository' to be more specific
    private val visitorLogRepository: VisitorLogRepository // NEW: Inject VisitorLogRepository
) : ViewModel() {

    val uiState = combine(
        visitorLogRepository.getAllVisitorLogs(), // Changed to call VisitorLogRepository
        employeeRepository.getAllEmployees() // Changed to call getAllEmployees() for consistency
    ) { logs, employees ->
        // Changed to use employee.id (String) and combine first and last name
        val employeeMap = employees.associateBy({ it.id }, { "${it.firstName} ${it.lastName}" })
        AdminLogUiState(
            logs = logs,
            employees = employeeMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminLogUiState())
}