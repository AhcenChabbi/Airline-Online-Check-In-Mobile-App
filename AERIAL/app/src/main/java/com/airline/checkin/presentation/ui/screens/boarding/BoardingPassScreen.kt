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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.BoardingPassViewModel

@Composable
fun BoardingPassScreen(
        onBack: () -> Unit,
        checkinId: String = "",
        viewModel: BoardingPassViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(checkinId) {
        viewModel.loadBoardingPass(checkinId)
    }

    BoardingPassContent(
            uiState = uiState,
            checkinId = checkinId,
            onDownload = { viewModel.downloadPdf(checkinId) },
            onDismissError = viewModel::clearError,
            onRetry = { viewModel.loadBoardingPass(checkinId) },
            onBack = onBack
    )
}

@Composable
private fun BoardingPassContent(
        uiState: com.airline.checkin.presentation.ui.state.BoardingPassUiState,
        checkinId: String,
        onDownload: () -> Unit,
        onDismissError: () -> Unit,
        onRetry: () -> Unit,
        onBack: () -> Unit
) {
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .padding(innerPadding)
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = Spacing.gutter)
            ) {
                if (uiState.isOffline) {
                    OfflineBanner(modifier = Modifier.padding(top = Spacing.md))
                }

                if (uiState.error != null) {
                    ErrorBanner(
                            message = uiState.error,
                            onDismiss = onDismissError,
                            onRetry = if (checkinId.isNotBlank()) onRetry else null,
                            modifier = Modifier.padding(top = Spacing.md)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

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
                            text = "Boarding pass unavailable. Complete check-in to generate it.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Row(modifier = Modifier.fillMaxWidth()) {
                    val pdfLabel = if (uiState.pdfPath != null) "PDF Saved" else "PDF Pass"
                    OutlinedButton(
                            onClick = onDownload,
                            enabled = !uiState.isDownloading && checkinId.isNotBlank(),
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors =
                                    ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                    )
                    ) {
                        Icon(Icons.Rounded.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pdfLabel, fontWeight = FontWeight.Bold)
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

            if (uiState.isLoading) {
                LoadingOverlay()
            }
        }
    }
}
