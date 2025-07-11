package com.exposystems.welcomewave.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.automirrored.filled.ExitToApp // NEW: Import for logout icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect // NEW: Import LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController // NEW: Import NavController
import com.exposystems.welcomewave.navigation.Screen // NEW: Import Screen for navigation routes
import com.exposystems.welcomewave.ui.adminlogin.AdminLoginViewModel // NEW: Import AdminLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmployeeListScreen(
    viewModel: AdminEmployeeListViewModel = hiltViewModel(),
    authViewModel: AdminLoginViewModel = hiltViewModel(),
    navController: NavController,
    onAddEmployeeClicked: () -> Unit,
    onEditEmployeeClicked: (String) -> Unit,
    onViewLogClicked: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val employees by viewModel.employees.collectAsState()
    val logoutSuccessful by authViewModel.logoutSuccessful.collectAsState()

    // NEW: LaunchedEffect to handle logout navigation
    LaunchedEffect(logoutSuccessful) {
        logoutSuccessful?.let { isSuccess ->
            if (isSuccess) {
                // Logout successful, navigate back to AdminLogin screen and clear back stack
                navController.navigate(Screen.AdminLogin.route) {
                    // This clears the entire back stack up to the root, then adds AdminLogin
                    // ensuring a clean state after logout.
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
                authViewModel.clearLogoutState()
            } else {
                // Handle logout failure (e.g., show a Toast or Snackbar)
                // You might add a SnackbarHostState to show messages
                // scaffoldState.snackbarHostState.showSnackbar("Logout failed. Please try again.")
                authViewModel.clearLogoutState() // Clear the state even on failure
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Employees") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewLogClicked) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "View Visitor Log"
                        )
                    }
                    // --- NEW: Logout Button ---
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                    // --- END NEW ---
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEmployeeClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(employees, key = { it.id }) { employee ->
                EmployeeManagementListItem(
                    employee = employee,
                    onEdit = { onEditEmployeeClicked(employee.id) },
                    onDelete = { viewModel.onDeleteEmployee(employee) }
                )
            }
        }
    }
}

@Composable
private fun EmployeeManagementListItem(
    employee: com.exposystems.welcomewave.data.model.Employee,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "${employee.firstName} ${employee.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(text = employee.title ?: "", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Employee")
            }
        }
    }
}