package com.exposystems.welcomewave.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.CheckInLog
import com.exposystems.welcomewave.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckOutUiState(
    val checkedInVisitors: List<CheckInLog> = emptyList(),
    val employees: Map<Int, String> = emptyMap() // Map of Employee ID to Name
)

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    // Combine the flow of checked-in visitors and the flow of all employees
    val uiState = combine(
        repository.getCheckedInVisitors(),
        repository.getEmployees()
    ) { visitors, employees ->
        // Create a map of employee IDs to names for easy lookup
        val employeeMap = employees.associateBy({ it.id }, { it.name })
        CheckOutUiState(
            checkedInVisitors = visitors,
            employees = employeeMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckOutUiState())

    fun onCheckOut(log: CheckInLog) {
        viewModelScope.launch {
            repository.logCheckOut(log)
        }
    }
}