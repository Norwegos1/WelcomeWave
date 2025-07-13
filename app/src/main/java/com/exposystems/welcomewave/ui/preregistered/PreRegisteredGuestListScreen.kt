package com.exposystems.welcomewave.ui.preregistered

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreRegisteredGuestListScreen(
    viewModel: PreRegisteredViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onCheckInComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pre-Registered Guests") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.guests.isEmpty() -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Text("Error: ${uiState.error}")
                }

                uiState.guests.isEmpty() -> {
                    Text("No pre-registered guests found.")
                }

                else -> {
                    GuestList(
                        guests = uiState.guests,
                        checkingInGuestId = uiState.checkingInGuestId,
                        onGuestClicked = { guest ->
                            viewModel.checkInGuest(guest, onCheckInComplete)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GuestList(
    guests: List<PreRegisteredGuest>,
    checkingInGuestId: String?,
    onGuestClicked: (PreRegisteredGuest) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(guests, key = { it.id }) { guest ->
            GuestListItem(
                guest = guest,
                isCheckingIn = guest.id == checkingInGuestId,
                onClick = { onGuestClicked(guest) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GuestListItem(
    guest: PreRegisteredGuest,
    isCheckingIn: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isCheckingIn, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            if (isCheckingIn) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column {
                    Text(
                        text = guest.visitorName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    guest.visitorCompany?.let { company ->
                        Text(
                            text = "From: $company",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "To see: ${guest.employeeToSeeName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}