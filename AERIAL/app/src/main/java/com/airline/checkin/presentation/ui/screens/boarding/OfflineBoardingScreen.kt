package com.airline.checkin.presentation.ui.screens.boarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.BoardingPassViewModel

@Composable
fun OfflineBoardingScreen(
        onBack: () -> Unit,
        checkinId: String = "",
        viewModel: BoardingPassViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

        LaunchedEffect(checkinId) {
                viewModel.loadOfflineBoardingPass(checkinId)
        }

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
                modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            OfflineBanner()

            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = Spacing.gutter)
            ) {
                Spacer(modifier = Modifier.height(Spacing.xl))

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

                val boardingPass = uiState.boardingPass
                if (boardingPass != null) {
                    val cardData = toCardData(boardingPass)
                    BoardingPassCard(
                            passengerName = cardData.passengerName,
                            flightNumber = cardData.flightNumber,
                            from = cardData.from,
                            fromCity = cardData.fromCity,
                            to = cardData.to,
                            toCity = cardData.toCity,
                            date = cardData.date,
                            gate = cardData.gate,
                            seat = cardData.seat,
                            boardingTime = cardData.boardingTime,
                            bookingRef = cardData.bookingRef,
                            qrCodeData = cardData.qrCodeData
                    )
                } else {
                    Text(
                            text = "No cached boarding pass found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))

                ConfirmButton(
                        text = "Back to My Flights",
                        onClick = onBack
                )

                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}
