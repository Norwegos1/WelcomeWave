package com.exposystems.welcomewave.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exposystems.welcomewave.data.model.Employee
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.AuthRepository // IMPORT AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // Import collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Import await for getIdTokenResult

import javax.inject.Inject
import com.google.firebase.auth.FirebaseUser // Import FirebaseUser
import com.google.firebase.auth.GetTokenResult // Import GetTokenResult

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
        // --- NEW: Observe authentication state and load data conditionally ---
        viewModelScope.launch {
            authRepository.currentUserFlow.collectLatest { firebaseUser: FirebaseUser? ->
                if (firebaseUser != null) {
                    _dataLoadState.value = DataLoadState.Loading // Start loading/checking auth

                    try {
                        // Force refresh token to get the latest custom claims (important after setting claim)
                        val idTokenResult: GetTokenResult = firebaseUser.1getIdTokenResult(true).await()
                        val isAdmin: Boolean = (idTokenResult.claims?.get("admin") as? Boolean) ?: false

                        if (isAdmin) {
                            // User is an admin, proceed to load employees
                            employeeRepository.getAllEmployees().collectLatest { employeeList ->
                                _employees.value = employeeList
                                _dataLoadState.value = DataLoadState.Success
                            }
                        } else {
                            // User is logged in but NOT an admin (or claim not yet propagated)
                            _employees.value = emptyList()
                            _dataLoadState.value = DataLoadState.PermissionDenied
                        }
                    } catch (e: Exception) {
                        // Handle errors during token refresh or claim check
                        _employees.value = emptyList()
                        _dataLoadState.value = DataLoadState.Error("Authentication check failed: ${e.message}")
                    }

                } else {
                    // User is logged out
                    _employees.value = emptyList()
                    _dataLoadState.value = DataLoadState.NotAuthenticated
                }
            }
        }
        // --- END NEW ---
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