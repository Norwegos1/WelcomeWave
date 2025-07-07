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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmployeeListScreen(
    viewModel: AdminEmployeeListViewModel = hiltViewModel(),
    onAddEmployeeClicked: () -> Unit,
    onEditEmployeeClicked: (String) -> Unit, // CHANGED: Parameter type from Int to String
    onViewLogClicked: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val employees by viewModel.employees.collectAsState()

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
            // It.id is now String, which is fine for the key parameter
            items(employees, key = { it.id }) { employee ->
                EmployeeManagementListItem(
                    employee = employee,
                    onEdit = { onEditEmployeeClicked(employee.id) }, // CHANGED: Pass employee.id (String)
                    onDelete = { viewModel.onDeleteEmployee(employee) }
                )
            }
        }
    }
}

@Composable
private fun EmployeeManagementListItem(
    employee: com.exposystems.welcomewave.data.model.Employee, // CHANGED: Ensure correct Employee model
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
                // CHANGED: Display first and last name
                Text(text = "${employee.firstName} ${employee.lastName}", style = MaterialTheme.typography.titleMedium)
                // CHANGED: Handle nullable title
                Text(text = employee.title ?: "", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Employee")
            }
        }
    }
}