package com.exposystems.welcomewave.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee // Ensure this imports your NEW Employee data class
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminEmployeeListViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    // Calls the new real-time flow from EmployeeRepository
    val employees = employeeRepository.getAllEmployees() // Changed from getEmployees() to getAllEmployees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDeleteEmployee(employee: Employee) {
        viewModelScope.launch {
            // Firestore deleteEmployee method now takes a String ID
            employeeRepository.deleteEmployee(employee.id) // Changed to employee.id (String)
        }
    }
}