package com.exposystems.welcomewave.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee
import com.exposystems.welcomewave.data.repository.AuthRepository
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminEmployeeListViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val authRepository: AuthRepository // INJECT AuthRepository
) : ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    // --- NEW: State to manage data loading and permission status ---
    private val _dataLoadState = MutableStateFlow<DataLoadState>(DataLoadState.Loading)
    val dataLoadState: StateFlow<DataLoadState> = _dataLoadState.asStateFlow()
    // --- END NEW ---

    init {
        viewModelScope.launch {
            authRepository.currentUserFlow.collectLatest { firebaseUser ->
                if (firebaseUser != null) {
                    _dataLoadState.value = DataLoadState.Loading

                    try {
                        val idTokenResult = firebaseUser.getIdToken(true).await()
                        val isAdmin = (idTokenResult.claims["admin"] as? Boolean) ?: false

                        if (isAdmin) {
                            // —— NEW: actually load employees ——
                            viewModelScope.launch {
                                employeeRepository.getAllEmployees()
                                    .catch { e ->
                                        _dataLoadState.value = DataLoadState.Error("Load failed: ${e.message}")
                                        Log.e("AdminVM", "Error loading employees", e)
                                    }
                                    .collectLatest { list ->
                                        Log.d("AdminVM", "Loaded ${list.size} employees")
                                        _employees.value = list
                                        _dataLoadState.value = DataLoadState.Success
                                    }
                            }
                        } else {
                            _employees.value = emptyList()
                            _dataLoadState.value = DataLoadState.PermissionDenied
                        }
                    } catch (e: Exception) {
                        _employees.value = emptyList()
                        _dataLoadState.value = DataLoadState.Error("Auth check failed: ${e.message}")
                    }

                } else {
                    _employees.value = emptyList()
                    _dataLoadState.value = DataLoadState.NotAuthenticated
                }
            }
        }
    }

    fun onDeleteEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employee.id)
        }
    }
}

// NEW: Sealed class to represent data loading state based on authentication/authorization
sealed class DataLoadState {
    object Loading : DataLoadState()
    object Success : DataLoadState() // Data loaded successfully (user is admin)
    object NotAuthenticated : DataLoadState() // User is not logged in
    object PermissionDenied : DataLoadState() // User is logged in but not an admin
    data class Error(val message: String) : DataLoadState() // General error during process
}