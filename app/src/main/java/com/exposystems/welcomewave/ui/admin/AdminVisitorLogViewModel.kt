package com.exposystems.welcomewave.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.CheckInLog
import com.exposystems.welcomewave.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AdminLogUiState(
    val logs: List<CheckInLog> = emptyList(),
    val employees: Map<Int, String> = emptyMap()
)

@HiltViewModel
class AdminVisitorLogViewModel @Inject constructor(
    repository: EmployeeRepository
) : ViewModel() {

    val uiState = combine(
        repository.getVisitorLogs(),
        repository.getEmployees()
    ) { logs, employees ->
        val employeeMap = employees.associateBy({ it.id }, { it.name })
        AdminLogUiState(
            logs = logs,
            employees = employeeMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminLogUiState())
}