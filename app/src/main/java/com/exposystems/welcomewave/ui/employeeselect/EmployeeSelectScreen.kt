package com.exposystems.welcomewave.ui.employeeselect

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.exposystems.welcomewave.data.Employee

@Composable
fun EmployeeSelectScreen(
    viewModel: EmployeeSelectViewModel = hiltViewModel(),
    onNextClicked: (Int) -> Unit // Callback to navigate with employee ID
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
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(filteredEmployees, key = { it.id }) { employee ->
                EmployeeListItem(
                    employee = employee,
                    isSelected = employee.id == uiState.selectedEmployee?.id,
                    onSelected = { viewModel.onEmployeeSelected(employee) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                uiState.selectedEmployee?.id?.let { onNextClicked(it) }
            },
            enabled = uiState.selectedEmployee != null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text("Next")
        }
    }
}

@Composable
fun EmployeeListItem(
    employee: Employee,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clickable { onSelected() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = employee.photoUri?.let { Uri.parse(it) },
                contentDescription = "${employee.name} photo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(employee.name, fontWeight = FontWeight.Bold)
                Text(employee.title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}