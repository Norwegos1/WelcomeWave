package com.exposystems.welcomewave.ui.employeeselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.Employee
import com.exposystems.welcomewave.data.EmployeeRepository
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
    employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _employees = employeeRepository.getEmployees()

    // Combine has been simplified to only use two flows
    val uiState = combine(
        _searchQuery,
        _employees
    ) { query, employees ->
        EmployeeSelectUiState(
            searchQuery = query,
            allEmployees = employees
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