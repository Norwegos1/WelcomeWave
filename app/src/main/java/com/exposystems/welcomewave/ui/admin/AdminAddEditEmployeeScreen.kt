package com.exposystems.welcomewave.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.io.File

/**
 * A screen for administrators to add a new employee or edit an existing one.
 * It provides a form with fields for employee details and photo selection.
 *
 * @param viewModel The ViewModel that holds the state and logic for this screen.
 * @param onNavigateUp A callback function to navigate back to the previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditEmployeeScreen(
    viewModel: AdminAddEditViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    // Get the UI state directly from the ViewModel.
    val uiState = viewModel.uiState

    // Create a scroll state to allow the form to be scrollable, especially on smaller screens or when the keyboard is open.
    val scrollState = rememberScrollState()

    // This is the modern Android way to handle picking media (like photos).
    // It creates a launcher that waits for a result from the photo picker activity.
    // When a photo is selected, its URI is passed to the ViewModel's onPhotoSelected function.
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickVisualMedia(),
        onResult = viewModel::onPhotoSelected
    )

    Scaffold(
        topBar = {
            TopAppBar(
                // The title of the screen dynamically changes based on whether we are adding a new employee or editing one.
                title = { Text(if (uiState.isNewEmployee) "Add Employee" else "Edit Employee") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // The main layout is a Column that holds all the form elements.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState) // Makes the column scrollable.
                .imePadding(), // Automatically adds padding when the keyboard is shown.
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Adds space between each element in the column.
        ) {
            // Coil's AsyncImage composable to load and display the employee's photo.
            // It intelligently handles loading from a web URL (http), a content URI from the picker, or a local file path.
            AsyncImage(
                model = uiState.photoUrl?.let { path ->
                    if (path.startsWith("content://") || path.startsWith("http")) {
                        path
                    } else {
                        File(path).toUri()
                    }
                },
                contentDescription = "Employee Photo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape), // Clips the image into a circle.
                contentScale = ContentScale.Crop // Scales the image to fill the circle shape.
            )

            Spacer(Modifier.height(8.dp))

            // Button to trigger the photo picker launcher.
            Button(onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(PickVisualMedia.ImageOnly) // Specifies that only images can be selected.
                )
            }) {
                Text("Select Photo")
            }

            Spacer(Modifier.height(24.dp))

            // A series of OutlinedTextFields for capturing employee details.
            // Each one is bound to a specific property in the ViewModel's UI state.
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text("First Name") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text("Last Name") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, capitalization = KeyboardCapitalization.None),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title / Position") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.department,
                onValueChange = viewModel::onDepartmentChange,
                label = { Text("Department") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // A Row to nicely position the label and the Switch for the 'isActive' status.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Active Employee")
                Switch(
                    checked = uiState.isActive,
                    onCheckedChange = viewModel::onIsActiveChange
                )
            }

            Spacer(Modifier.height(16.dp))

            // The main action button to save the employee details.
            Button(
                onClick = { viewModel.onSaveEmployeeClicked(onNavigateUp) },
                // The button is disabled while the save operation is in progress.
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Shows a progress indicator inside the button when loading.
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (uiState.isNewEmployee) "Add Employee" else "Save Employee")
                }
            }

            // Conditionally displays an error message if something goes wrong during the save operation.
            if (uiState.showError) {
                Text(
                    text = uiState.errorMessage ?: "An error occurred.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}