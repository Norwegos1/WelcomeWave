package com.exposystems.welcomewave.ui.adminlogin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Import for scroll state
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // Import for vertical scroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import all runtime for remember, mutableStateOf, LaunchedEffect, getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    viewModel: AdminLoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val forgotPasswordResult by viewModel.forgotPasswordResult.collectAsState()

    // NEW: State for showing Forgot Password dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmailInput by remember { mutableStateOf("") }

    // LaunchedEffect to handle automatic redirection on login success
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    // LaunchedEffect to handle Forgot Password result feedback
    LaunchedEffect(forgotPasswordResult) {
        forgotPasswordResult?.let { result ->
            val message = if (result) "Password reset email sent. Check your inbox." else "Failed to send reset email. Check email or try again."
            // In a real app, use a SnackbarHostState to show a Snackbar message
            // scaffoldState.snackbarHostState.showSnackbar(message)
            println(message) // For logging to console/logcat during development
            showForgotPasswordDialog = false // Close dialog after action
            viewModel.clearForgotPasswordResult() // Clear the state
        }
    }

    val scrollState = rememberScrollState() // NEW: For scrollable content

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Login") },
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
                .verticalScroll(scrollState) // Make column scrollable
                .imePadding() // Adjust padding when keyboard is active
                .padding(32.dp), // General padding for the content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Keep center arrangement for overall layout
        ) {
            Text(
                "Welcome Administrator",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onLoginClicked() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Login", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(Modifier.height(8.dp)) // Spacing for new button

            // NEW: Forgot Password Button
            TextButton(
                onClick = { showForgotPasswordDialog = true },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Forgot Password?")
            }


            if (uiState.showError) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "An unknown error occurred.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }


        // NEW: Forgot Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Reset Password") },
                text = {
                    Column {
                        Text("Enter your email to receive a password reset link.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = forgotPasswordEmailInput,
                            onValueChange = { forgotPasswordEmailInput = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.sendPasswordResetEmail(forgotPasswordEmailInput)
                    }) {
                        Text("Send Reset Link")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}