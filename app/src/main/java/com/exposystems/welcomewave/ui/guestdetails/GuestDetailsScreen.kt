package com.exposystems.welcomewave.ui.guestdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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

@Composable
fun GuestDetailsScreen(
    viewModel: GuestDetailsViewModel = hiltViewModel(),
    onCheckInComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        uiState.selectedEmployee?.let {
            Text(
                "Checking in to see:",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                it.name,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.companyName,
            onValueChange = viewModel::onCompanyChange,
            label = { Text("Your Company Name") },
            modifier = Modifier.fillMaxWidth(0.7f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Guest List
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(0.7f)) {
            items(uiState.guests, key = { it.id }) { guest ->
                GuestInputItem(
                    guest = guest,
                    onNameChange = { newName ->
                        viewModel.onGuestNameChange(guest.id, newName)
                    },
                    onRemove = { viewModel.onRemoveGuest(guest.id) },
                    canBeRemoved = uiState.guests.size > 1
                )
            }
        }

        Button(
            onClick = { viewModel.onAddGuest() },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Guest")
            Spacer(Modifier.width(8.dp))
            Text("Add Another Guest")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.checkInGuests(onCheckInComplete) },
            enabled = uiState.isCheckInEnabled,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp)
        ) {
            Text("Complete Check-In")
        }
    }
}

@Composable
fun GuestInputItem(
    guest: Guest,
    onNameChange: (String) -> Unit,
    onRemove: () -> Unit,
    canBeRemoved: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = guest.name,
            onValueChange = onNameChange,
            label = { Text("Guest Name") },
            modifier = Modifier.weight(1f)
        )
        if (canBeRemoved) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Guest")
            }
        }
    }
}