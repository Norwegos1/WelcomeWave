package com.exposystems.welcomewave.ui.welcome

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
        // The VideoPlayer is placed first, so it's in the background
        VideoPlayer(
            videoResourceId = R.raw.logo_splash,
            modifier = Modifier.fillMaxSize()
        )

        // The text is placed in a Column on top of the video
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp), // Push the text up from the bottom
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom // Align to the bottom
        ) {
            Text(
                text = stringResource(id = R.string.welcome_prompt),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White, // Use white text for better contrast on the video
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onCheckOutNavigate) {
                Text("Visitor Check-out")
            }
        }

    }
}