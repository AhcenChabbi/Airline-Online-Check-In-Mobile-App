package com.airline.checkin.presentation.ui.screens.flight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.ConfirmButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun FlightDetailScreen(
    onContinueToCheckIn: () -> Unit,
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = onNavigateToNotifications,
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            Text(
                text = "Flight Details",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            ConfirmButton(
                text = "Continue to Check-in",
                onClick = onContinueToCheckIn,
                modifier = Modifier.padding(bottom = Spacing.lg)
            )
        }
    }
}
