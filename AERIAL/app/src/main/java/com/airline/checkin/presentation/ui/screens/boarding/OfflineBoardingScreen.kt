package com.airline.checkin.presentation.ui.screens.boarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OfflineBoardingScreen(
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boardingPass = uiState.boardingPass
    val passengerName = listOf(
        boardingPass?.offlinePayload?.passenger?.firstName,
        boardingPass?.offlinePayload?.passenger?.lastName
    ).filter { !it.isNullOrBlank() }.joinToString(" ").ifBlank {
        uiState.passenger?.name.orEmpty()
    }
    val flightNumber = boardingPass?.offlinePayload?.flight?.flightNumber ?: uiState.flight?.flightNumber.orEmpty()
    val from = boardingPass?.offlinePayload?.flight?.origin ?: uiState.flight?.originIata.orEmpty()
    val to = boardingPass?.offlinePayload?.flight?.destination ?: uiState.flight?.destinationIata.orEmpty()
    val date = uiState.flight?.date.orEmpty()
    val boardingTime = uiState.flight?.departureTime.orEmpty()
    val seatCode = boardingPass?.offlinePayload?.seat?.seatCode ?: uiState.selectedSeat?.seatCode.orEmpty()
    val bookingRef = uiState.bookingReference.ifBlank { uiState.checkIn?.bookingId.orEmpty() }
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
                    passengerName = passengerName,
                    flightNumber = flightNumber,
                    from = from,
                    fromCity = uiState.flight?.originCity.orEmpty(),
                    to = to,
                    toCity = uiState.flight?.destinationCity.orEmpty(),
                    date = date,
                    gate = uiState.flight?.gate.orEmpty(),
                    seat = seatCode,
                    boardingTime = boardingTime,
                    bookingRef = bookingRef,
                    qrCodeData = boardingPass?.qrCodeData.orEmpty()
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
