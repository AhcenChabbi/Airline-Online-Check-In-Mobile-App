package com.airline.checkin.presentation.ui.screens.boarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun OfflineBoardingScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {}
            )
        },
        containerColor = Color(0xFFF9FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OfflineBanner()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.gutter)
            ) {
                Spacer(modifier = Modifier.height(Spacing.xl))

                // 1. Page Header
                Text(
                    text = "Offline Boarding Pass",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = "This boarding pass is cached and available without internet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                // 2. The Boarding Pass Card
                BoardingPassCard(
                    passengerName = "Alex Mercer",
                    flightNumber = "AF1234",
                    from = "CDG",
                    fromCity = "Paris",
                    to = "ALG",
                    toCity = "Algiers",
                    date = "24 Oct 2023",
                    gate = "B14",
                    seat = "12A",
                    boardingTime = "10:00 AM",
                    bookingRef = "A8X9B2",
                    qrCodeData = "JWT_OFFLINE_PAYLOAD_TOKEN"
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                // 3. Actions
                ConfirmButton(
                    text = "Back to My Flights",
                    onClick = onBack
                )

                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}
