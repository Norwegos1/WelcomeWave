package com.exposystems.welcomewave.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onCheckOutNavigate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.onScreenTapped(
                            onGuestNavigate = onGuestNavigate,
                            onAdminNavigate = onAdminNavigate
                        )
                    }
                )
            }
    ) {
        VideoPlayer(
            videoResourceId = R.raw.logo_splash,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Top Section ---
            // A Column to group the greeting and title together
            Column(
                modifier = Modifier.padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The dynamic greeting with a slightly smaller font
                Text(
                    text = uiState.greeting,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                )
                // The static welcome title with a more subtle appearance
                Text(
                    text = stringResource(id = R.string.welcome_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f) // Make it slightly transparent
                )
            }

            // This spacer pushes the logo and bottom content apart
            Spacer(modifier = Modifier.weight(1f))

            // --- Bottom Section ---
            Text(
                text = stringResource(id = R.string.welcome_prompt),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

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
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}