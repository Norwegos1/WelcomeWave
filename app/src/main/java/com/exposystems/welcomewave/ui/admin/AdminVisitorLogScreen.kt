package com.exposystems.welcomewave.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exposystems.welcomewave.data.model.VisitorLog // CHANGED: Import the NEW VisitorLog data model
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVisitorLogScreen(
    viewModel: AdminVisitorLogViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visitor Log History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.logs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No visitor logs found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // log is now type VisitorLog (from uiState.logs)
                items(uiState.logs, key = { it.id }) { log ->
                    LogHistoryItem(
                        log = log,
                        // CHANGED: Use log.employeeVisitedId for lookup (String ID)
                        employeeName = uiState.employees[log.employeeVisitedId] ?: "Unknown"
                    )
                }
            }
        }
    }
}

@Composable
private fun LogHistoryItem(log: VisitorLog, employeeName: String) { // CHANGED: log is VisitorLog
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = log.visitorName, style = MaterialTheme.typography.titleMedium) // CHANGED: from visitorNames to visitorName
            log.companyName?.let {
                Text(
                    text = "from $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Visited: $employeeName",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                // CHANGED: checkInTime is now Date? directly
                text = "Checked In: ${log.checkInTime?.let { dateFormat.format(it) } ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                // CHANGED: checkOutTime is now Date? directly
                text = "Checked Out: ${log.checkOutTime?.let { dateFormat.format(it) } ?: "Still signed in"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}