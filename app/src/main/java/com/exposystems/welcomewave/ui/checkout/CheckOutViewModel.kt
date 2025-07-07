package com.exposystems.welcomewave.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.VisitorLog
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckOutUiState(
    val checkedInVisitors: List<VisitorLog> = emptyList(),
    val employees: Map<String, String> = emptyMap()
)

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    @Suppress("UnusedPrivateProperty")
    private val employeeRepository: EmployeeRepository,
    private val visitorLogRepository: VisitorLogRepository
) : ViewModel() {

    // Combine the flow of checked-in visitors (from VisitorLogRepository)
    // and the flow of all employees (from EmployeeRepository)
    val uiState = combine(
        visitorLogRepository.getCurrentlyCheckedInVisitors(),
        employeeRepository.getAllEmployees()
    ) { visitors, employees ->
        // Create a map of employee IDs (String) to combined names for easy lookup
        val employeeMap = employees.associateBy({ it.id }, { "${it.firstName} ${it.lastName}" })
        CheckOutUiState(
            checkedInVisitors = visitors,
            employees = employeeMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckOutUiState())

    fun onCheckOut(log: VisitorLog) {
        viewModelScope.launch {
            // Call logCheckOut on VisitorLogRepository, passing the log's Firestore ID
            visitorLogRepository.logCheckOut(log.id)
        }
    }
}