package com.airline.checkin.presentation.ui.screens.boarding

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownload
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
import com.airline.checkin.presentation.ui.viewmodels.BoardingPassViewModel
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect

@Composable
fun BoardingPassScreen(
    onBack: () -> Unit,
    viewModel: BoardingPassViewModel = hiltViewModel(),
    checkInViewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkInState by checkInViewModel.uiState.collectAsStateWithLifecycle()
    val checkInId = checkInState.checkIn?.id

    LaunchedEffect(checkInId) {
        if (checkInId != null) {
            viewModel.loadBoardingPass(checkInId)
        }
    }

    val boardingPass = uiState.boardingPass
    val passengerName = listOf(
        boardingPass?.offlinePayload?.passenger?.firstName,
        boardingPass?.offlinePayload?.passenger?.lastName
    ).filter { !it.isNullOrBlank() }.joinToString(" ").ifBlank {
        checkInState.passenger?.name.orEmpty()
    }
    val flightNumber = boardingPass?.offlinePayload?.flight?.flightNumber ?: checkInState.flight?.flightNumber.orEmpty()
    val from = boardingPass?.offlinePayload?.flight?.origin ?: checkInState.flight?.originIata.orEmpty()
    val to = boardingPass?.offlinePayload?.flight?.destination ?: checkInState.flight?.destinationIata.orEmpty()
    val date = checkInState.flight?.date.orEmpty()
    val boardingTime = checkInState.flight?.departureTime.orEmpty()
    val seatCode = boardingPass?.offlinePayload?.seat?.seatCode ?: checkInState.selectedSeat?.seatCode.orEmpty()
    val bookingRef = checkInState.bookingReference.ifBlank { checkInState.checkIn?.bookingId.orEmpty() }
    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {}
            )
        },
        containerColor = Color(0xFFF9FAFC),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            // 1. Page Header
            Text(
                text = "Your Boarding Pass",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "Present this digital pass at the boarding gate.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 2. The Boarding Pass Card
            BoardingPassCard(
                passengerName = passengerName,
                flightNumber = flightNumber,
                from = from,
                fromCity = checkInState.flight?.originCity.orEmpty(),
                to = to,
                toCity = checkInState.flight?.destinationCity.orEmpty(),
                date = date,
                gate = checkInState.flight?.gate.orEmpty(),
                seat = seatCode,
                boardingTime = boardingTime,
                bookingRef = bookingRef,
                qrCodeData = boardingPass?.qrCodeData.orEmpty()
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 3. Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        if (checkInId != null) {
                            viewModel.downloadPdf(checkInId) { }
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PDF Pass", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(Spacing.md))
                
                ConfirmButton(
                    text = "Done",
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}
