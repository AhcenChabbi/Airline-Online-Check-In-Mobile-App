package com.airline.checkin.presentation.ui.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConfirmationScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val passengerName = uiState.passenger?.name.orEmpty()
    val flightNumber = uiState.flight?.flightNumber.orEmpty()
    val origin = uiState.flight?.originIata.orEmpty()
    val destination = uiState.flight?.destinationIata.orEmpty()
    val originCity = uiState.flight?.originCity.orEmpty()
    val destinationCity = uiState.flight?.destinationCity.orEmpty()
    val seatCode = uiState.selectedSeat?.seatCode.orEmpty()
    val seatType = uiState.selectedSeat?.seatType.orEmpty()
    val baggageCount = uiState.baggage.sumOf { it.quantity }
    val pnr = uiState.bookingReference.ifBlank { uiState.checkIn?.bookingId.orEmpty() }
    var acknowledged by remember { mutableStateOf(false) }
    var pendingContinue by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading, uiState.error, uiState.boardingPass) {
        if (pendingContinue && !uiState.isLoading) {
            if (uiState.error == null && uiState.boardingPass != null) {
                onContinue()
            }
            pendingContinue = false
        }
    }

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {},
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF9FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            if (uiState.error != null) {
                ErrorBanner(
                    message = uiState.error ?: "Unable to complete check-in.",
                    onDismiss = { viewModel.clearError() }
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // 1. Success Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Gold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Text(
                    text = "You're almost there!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF051849)
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = "Review your details below and complete your check-in to receive your boarding pass.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.md)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 2. Summary Card
            FinalSummaryCard(
                passengerName = passengerName,
                flightNumber = flightNumber,
                origin = origin,
                destination = destination,
                originCity = originCity,
                destinationCity = destinationCity,
                seat = seatCode,
                seatType = seatType,
                baggageCount = baggageCount,
                pnr = pnr
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Acknowledgment
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = acknowledged,
                    onCheckedChange = { acknowledged = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF051849))
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "I acknowledge that I have read and agree to the Conditions of Carriage and confirm my baggage does not contain any prohibited items.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 4. Action Section
            // Triggers the creation of a BoardingPass record in the backend
            ConfirmButton(
                text = "Complete Check-in",
                enabled = acknowledged,
                onClick = {
                    uiState.checkIn?.id?.let {
                        pendingContinue = true
                        viewModel.confirmCheckIn(it)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Text(
                text = "You will receive a digital boarding pass.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}
