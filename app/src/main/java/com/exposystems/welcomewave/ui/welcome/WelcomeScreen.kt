package com.exposystems.welcomewave.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exposystems.welcomewave.R

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
    onGuestNavigate: () -> Unit,
    onAdminNavigate: () -> Unit,
    onCheckOutNavigate: () -> Unit,
    onPreRegisteredNavigate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoPlayer(
            videoResourceId = R.raw.logo_splash,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { viewModel.onAdminGestureTapped(onAdminNavigate) })
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.greeting,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = stringResource(id = R.string.welcome_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                // Button for pre-registered guests
                OutlinedButton(
                    onClick = onPreRegisteredNavigate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Pre-Registered Check-In", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button for walk-in guests
                OutlinedButton(
                    onClick = onGuestNavigate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Walk-In Check-In", style = MaterialTheme.typography.titleLarge)
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Check-out button
            OutlinedButton(
                onClick = onCheckOutNavigate,
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Visitor Check-out",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}