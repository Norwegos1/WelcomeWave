package com.exposystems.welcomewave.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.exposystems.welcomewave.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onScreenTapped: () -> Unit,
    onAdminNavigate: () -> Unit
) {
    var tapCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var guestNavigationJob by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { // This is our single, advanced click handler
                detectTapGestures(
                    onTap = {
                        // Cancel any previously scheduled navigation
                        guestNavigationJob?.cancel()

                        tapCount++
                        if (tapCount >= 5) {
                            // Admin action: navigate immediately
                            tapCount = 0 // Reset counter
                            onAdminNavigate()
                        } else {
                            // Guest action: schedule navigation after a short delay
                            // This gives the user time to perform more taps
                            guestNavigationJob = coroutineScope.launch {
                                delay(300) // Wait 300ms
                                onScreenTapped()
                                tapCount = 0 // Reset counter
                            }
                        }
                    }
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_company_logo),
            contentDescription = "Company Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to ExpoSystems",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tap anywhere to begin check-in",
            style = MaterialTheme.typography.titleLarge
        )
    }
}