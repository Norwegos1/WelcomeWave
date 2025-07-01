package com.exposystems.welcomewave.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exposystems.welcomewave.data.Employee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmployeeListScreen(
    viewModel: AdminEmployeeListViewModel = hiltViewModel(),
    onAddEmployeeClicked: () -> Unit,
    onNavigateUp: () -> Unit // The missing parameter
) {
    val employees by viewModel.employees.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Employees") },
                navigationIcon = { // Add the navigation icon to use the callback
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
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
                    onDelete = { viewModel.onDeleteEmployee(employee) }
                )
            }
        }
    }
}

@Composable
private fun EmployeeManagementListItem(
    employee: Employee,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = employee.name, style = MaterialTheme.typography.titleMedium)
                Text(text = employee.title, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Employee")
            }
        }
    }
}