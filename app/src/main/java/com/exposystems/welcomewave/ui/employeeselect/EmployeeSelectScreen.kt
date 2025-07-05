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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.exposystems.welcomewave.R
import com.exposystems.welcomewave.data.Employee
import java.io.File

@Composable
fun EmployeeSelectScreen(
    viewModel: EmployeeSelectViewModel = hiltViewModel(),
    onEmployeeSelected: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredEmployees = uiState.allEmployees.filter {
        it.name.contains(uiState.searchQuery, ignoreCase = true)
    }

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
        if (filteredEmployees.isEmpty() && uiState.allEmployees.isNotEmpty()) {
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(filteredEmployees, key = { it.id }) { employee ->
                    EmployeeListItem(
                        employee = employee,
                        onSelected = { onEmployeeSelected(employee.id) }
                    )
                }
            }
        }
    }
}

// The EmployeeListItem function remains the same as before
@Composable
fun EmployeeListItem(
    employee: Employee,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clickable { onSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Increased padding for more space
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = employee.photoUri?.let { File(it) },
                contentDescription = "${employee.name} photo",
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
                    text = employee.name,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = employee.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}