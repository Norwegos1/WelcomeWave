package com.exposystems.welcomewave.ui.employeeselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class EmployeeSelectUiState(
    val searchQuery: String = "",
    val allEmployees: List<Employee> = emptyList()
)

@HiltViewModel
class EmployeeSelectViewModel @Inject constructor(
    @Suppress("UnusedPrivateProperty")
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    // Use the new getAllEmployees() method for real-time updates
    private val _employees = employeeRepository.getAllEmployees()

    val uiState = combine(
        _searchQuery,
        _employees
    ) { query, employees ->
        val filteredEmployees = if (query.isBlank()) {
            employees
        } else {
            employees.filter { employee ->
                // Filter by firstName or lastName (or both)
                employee.firstName.contains(query, ignoreCase = true) ||
                        employee.lastName.contains(query, ignoreCase = true) ||
                        employee.email.contains(query, ignoreCase = true)
            }
        }
        EmployeeSelectUiState(
            searchQuery = query,
            allEmployees = filteredEmployees // Use the filtered list
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmployeeSelectUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
    }

    fun onClearSearch() {
        _searchQuery.update { "" }
    }
}