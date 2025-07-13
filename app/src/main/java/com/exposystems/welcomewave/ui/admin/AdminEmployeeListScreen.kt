package com.exposystems.welcomewave.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.exposystems.welcomewave.navigation.Screen
import com.exposystems.welcomewave.ui.adminlogin.AdminLoginViewModel

/**
 * The main screen for administrators to manage the list of employees.
 * It displays all employees and provides actions to add, edit, delete, view logs, and logout.
 *
 * @param viewModel The ViewModel responsible for fetching and deleting employees.
 * @param authViewModel The ViewModel that handles authentication logic like logout.
 * @param navController The NavController for handling navigation events.
 * @param onAddEmployeeClicked Callback to navigate to the add employee screen.
 * @param onEditEmployeeClicked Callback to navigate to the edit employee screen with a specific ID.
 * @param onViewLogClicked Callback to navigate to the visitor log screen.
 * @param onNavigateUp Callback to navigate back to the previous screen.
 */
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
    // Observe the list of employees and the logout status from their respective ViewModels.
    val employees by viewModel.employees.collectAsState()
    val logoutSuccessful by authViewModel.logoutSuccessful.collectAsState()

    // LaunchedEffect is a side-effect handler that runs when its key (logoutSuccessful) changes.
    // This is the correct way to trigger navigation in response to a state change from a ViewModel.
    LaunchedEffect(logoutSuccessful) {
        // The `let` scope runs only if logoutSuccessful is not null.
        logoutSuccessful?.let { isSuccess ->
            if (isSuccess) {
                // If logout was successful, navigate to the login screen.
                navController.navigate(Screen.AdminLogin.route) {
                    // This clears the entire navigation back stack up to the very first screen.
                    // It ensures that after logging out, the user cannot press the back button
                    // to get back into the admin section.
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            } else {
                // If logout failed, you could show an error message here (e.g., a Snackbar).
            }
            // It's crucial to clear the state in the ViewModel to prevent this effect
            // from running again on configuration changes (like screen rotation).
            authViewModel.clearLogoutState()
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
                // Action icons shown on the right side of the TopAppBar.
                actions = {
                    IconButton(onClick = onViewLogClicked) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "View Visitor Log"
                        )
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        // The FloatingActionButton provides a primary action for the screen.
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEmployeeClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { paddingValues ->
        // LazyColumn is Compose's efficient way to display a long, scrollable list.
        // It only renders the items currently visible on screen.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Adds space between each list item.
        ) {
            // Create a list item for each employee in the state.
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

/**
 * A reusable Composable that represents a single row in the employee list.
 * @param employee The employee data to display.
 * @param onEdit Callback triggered when the item is clicked.
 * @param onDelete Callback triggered when the delete icon is clicked.
 */
@Composable
private fun EmployeeManagementListItem(
    employee: com.exposystems.welcomewave.data.model.Employee,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() } // The entire card is clickable to edit the employee.
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Pushes the text and icon to opposite ends.
        ) {
            // Column for displaying the employee's name and title.
            Column {
                Text(text = "${employee.firstName} ${employee.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(text = employee.title ?: "", style = MaterialTheme.typography.bodyMedium)
            }
            // A dedicated button for the delete action.
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Employee")
            }
        }
    }
}