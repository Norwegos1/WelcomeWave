package com.exposystems.welcomewave.ui.guestdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// We will build out the ViewModel and functionality for this screen later.
// This placeholder just shows the ID for now.
@Composable
fun GuestDetailsScreen(onCheckInComplete: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Guest Details Screen")
    }
}