package com.exposystems.welcomewave.ui.guestdetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GuestDetailsScreen(
    viewModel: GuestDetailsViewModel = hiltViewModel(),
    onCheckInComplete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    uiState.selectedEmployee?.let { employee ->
                        Text("Check-in for ${employee.firstName} ${employee.lastName}")
                    }
                },
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
    ) { paddingValues ->
        // Added check for loading state
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else { // WHEN YOU ARE IN THIS 'ELSE' BLOCK, uiState.isLoading IS GUARANTEED TO BE FALSE
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedTextField(
                        value = uiState.companyName,
                        onValueChange = viewModel::onCompanyChange,
                        label = { Text("Your Company Name") },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Please enter guest names below:", style = MaterialTheme.typography.titleMedium)
                }

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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.onAddGuest() },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Guest")
                        Spacer(Modifier.width(8.dp))
                        Text("Add Another Guest", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.checkInGuests(onCheckInComplete) },
                        enabled = uiState.isCheckInEnabled, // CHANGED: Removed '&& !uiState.isLoading'
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(64.dp)
                    ) {
                        Text("Complete Check-In", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GuestInputItem(
    guest: Guest,
    onNameChange: (String) -> Unit,
    onRemove: () -> Unit,
    canBeRemoved: Boolean
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = 4.dp)
            .bringIntoViewRequester(bringIntoViewRequester),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = guest.name,
            onValueChange = onNameChange,
            label = { Text("Guest Name") },
            modifier = Modifier
                .weight(1f)
                .onFocusEvent {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            textStyle = MaterialTheme.typography.titleLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        if (canBeRemoved) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Guest")
            }
        }
    }
}