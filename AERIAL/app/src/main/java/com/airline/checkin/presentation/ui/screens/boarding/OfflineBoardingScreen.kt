package com.airline.checkin.presentation.ui.screens.boarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.BoardingPassCard
import com.airline.checkin.presentation.ui.components.OfflineBanner
import com.airline.checkin.presentation.ui.components.PrimaryButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun OfflineBoardingScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OfflineBanner()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.gutter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                Text(
                    text = "Offline Boarding Pass",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "This boarding pass is cached and available offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.mdPlus))

                BoardingPassCard(
                    passengerName = "John Doe",
                    flightNumber = "AE 421",
                    from = "ALG",
                    to = "CDG",
                    date = "01 May 2026",
                    gate = "B12",
                    seat = "14A",
                    boardingTime = "10:30",
                    bookingRef = "ABC123"
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(text = "Back", onClick = onBack)

                Spacer(modifier = Modifier.height(Spacing.md))
            }
        }
    }
}
