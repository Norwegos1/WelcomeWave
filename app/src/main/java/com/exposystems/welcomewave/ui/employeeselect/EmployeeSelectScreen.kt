package com.exposystems.welcomewave.ui.employeeselect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.exposystems.welcomewave.R
import java.io.File

@Composable
fun EmployeeSelectScreen(
    viewModel: EmployeeSelectViewModel = hiltViewModel(),
    onEmployeeSelected: (String) -> Unit // CHANGED: Parameter type from Int to String
) {
    val uiState by viewModel.uiState.collectAsState()

    // Filtered employees logic is now handled in the ViewModel's combine
    // So, we use uiState.allEmployees directly and search logic is implicitly applied
    // based on how uiState.allEmployees is set up in the ViewModel.
    val employeesToDisplay = uiState.allEmployees // This will already be filtered by the ViewModel's combine logic

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Who are you here to see?",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            label = { Text("Search by name") },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = CircleShape,
            singleLine = true,
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onClearSearch() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(16.dp))

        // Check if the list is empty and show a message
        if (employeesToDisplay.isEmpty() && uiState.searchQuery.isNotEmpty()) { // If search query, and no results
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No employees match your search.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else if (employeesToDisplay.isEmpty() && uiState.searchQuery.isBlank()){ // If no search and no employees
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No employees added yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(employeesToDisplay, key = { it.id }) { employee -> // employee.id is String
                    EmployeeListItem(
                        employee = employee,
                        onSelected = { onEmployeeSelected(employee.id) } // CHANGED: Pass employee.id (String)
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeListItem(
    employee: com.exposystems.welcomewave.data.model.Employee, // CHANGED: Ensure correct Employee model
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clickable { onSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                // Updated to use employee.photoUrl and handle local file paths if necessary
                model = employee.photoUrl?.let { path ->
                    if (path.startsWith("content://") || path.startsWith("http")) {
                        path
                    } else {
                        File(path).toUri() // Convert local file path to Uri for Coil
                    }
                },
                contentDescription = "${employee.firstName} ${employee.lastName} photo", // CHANGED: Use first/last name
                placeholder = painterResource(id = R.drawable.avatar_placeholder),
                error = painterResource(id = R.drawable.avatar_placeholder),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    // CHANGED: Use firstName and lastName
                    text = "${employee.firstName} ${employee.lastName}",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    // CHANGED: Handle nullable title
                    text = employee.title ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}