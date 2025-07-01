package com.exposystems.welcomewave.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.Employee
import com.exposystems.welcomewave.data.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminEmployeeListViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    val employees = employeeRepository.getEmployees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDeleteEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employee)
        }
    }
}