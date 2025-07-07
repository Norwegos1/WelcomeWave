package com.exposystems.welcomewave.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage // Corrected import from previous suggestion
import java.io.File // Needed for File().toUri() if photoUrl is a local path
import androidx.core.net.toUri // Needed to convert File to Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditEmployeeScreen(
    viewModel: AdminAddEditViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val uiState = viewModel.uiState

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickVisualMedia(),
        onResult = viewModel::onPhotoSelected
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add/Edit Employee") }, // Changed title to reflect both actions
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Updated to use uiState.photoUrl and handle local file paths if necessary
            AsyncImage(
                // Assuming photoUrl can be a local file path or a remote URL
                model = uiState.photoUrl?.let { path ->
                    if (path.startsWith("content://") || path.startsWith("http")) {
                        path
                    } else {
                        File(path).toUri() // Convert local file path to Uri for Coil
                    }
                },
                contentDescription = "Employee Photo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Select Photo")
            }

            Spacer(Modifier.height(24.dp))

            // Updated for firstName
            OutlinedTextField(
                value = uiState.firstName, // Use firstName
                onValueChange = viewModel::onFirstNameChange, // Use onFirstNameChange
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // NEW: OutlinedTextField for lastName
            OutlinedTextField(
                value = uiState.lastName, // Use lastName
                onValueChange = viewModel::onLastNameChange, // Use onLastNameChange
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // No change needed for title or email fields, just ensure they are connected to uiState.title and uiState.email
            OutlinedTextField(
                value = uiState.title, // Access uiState.title directly
                onValueChange = viewModel::onTitleChange, // Calls viewModel::onTitleChange
                label = { Text("Title / Position") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveEmployee()
                    onNavigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save Employee")
            }
        }
    }
}