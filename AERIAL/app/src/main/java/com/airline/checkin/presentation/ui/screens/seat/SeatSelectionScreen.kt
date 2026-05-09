package com.airline.checkin.presentation.ui.screens.seat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.PrimaryButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun SeatSelectionScreen(onSeatConfirmed: () -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { AirlineTopBar(companyName = "AERIAL", onNotificationClick = {}) }) {
            innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.gutter)) {
            Text(
                    "Select Your Seat",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(text = "Confirm Seat", onClick = onSeatConfirmed)
        }
    }
}
