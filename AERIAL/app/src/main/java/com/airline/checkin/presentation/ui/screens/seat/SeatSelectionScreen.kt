package com.airline.checkin.presentation.ui.screens.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SeatSelectionScreen(
    onSeatConfirmed: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkInId = uiState.checkIn?.id
    val seatMap = uiState.seatMap
    var selectedSeatCode by remember { mutableStateOf(uiState.selectedSeat?.seatCode.orEmpty()) }

    LaunchedEffect(uiState.selectedSeat?.seatCode) {
        selectedSeatCode = uiState.selectedSeat?.seatCode.orEmpty()
    }

    LaunchedEffect(checkInId) {
        if (checkInId != null && seatMap.isEmpty()) {
            viewModel.loadSeatMap(checkInId)
        }
    }

    val rows = seatMap.map { it.rowNumber }.distinct().sorted()
    val columns = seatMap.map { it.columnLetter }.distinct().sorted()
    val aisleIndex = (columns.size / 2).coerceAtLeast(1)

    val selectedSeat = seatMap.firstOrNull { it.seatCode == selectedSeatCode }
    val seatType = when (selectedSeat?.seatType) {
        "WINDOW" -> "Window Seat"
        "AISLE" -> "Aisle Seat"
        "MIDDLE" -> "Middle Seat"
        else -> "Standard"
    }
    val seatFee = "Included"

    var pendingContinue by remember { mutableStateOf(false) }
    var pendingSeatId by remember { mutableStateOf<String?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.isLoading, uiState.error, uiState.selectedSeat) {
        if (pendingContinue && !uiState.isLoading) {
            if (uiState.error == null && (pendingSeatId == null || uiState.selectedSeat?.id == pendingSeatId)) {
                onSeatConfirmed()
            }
            pendingContinue = false
            pendingSeatId = null
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
        containerColor = Color(0xFFF9FAFC),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        bottomBar = {
            SeatConfirmationBar(
                seatCode = selectedSeatCode.ifBlank { "—" },
                seatType = seatType,
                fee = seatFee,
                onConfirm = {
                    if (checkInId != null && selectedSeat != null) {
                        localError = null
                        pendingContinue = true
                        pendingSeatId = selectedSeat.id
                        viewModel.selectSeat(checkInId, selectedSeat.id)
                    } else {
                        localError = "Select a seat to continue."
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Move scroll here for consistency
        ) {
            // Header Section - Consistent with PassportScanScreen
            Column(modifier = Modifier.padding(horizontal = Spacing.gutter)) {
                Spacer(modifier = Modifier.height(Spacing.md))
                StepProgressBar(currentStep = 2, totalSteps = 5)
                Spacer(modifier = Modifier.height(Spacing.lg))
                Text(
                    text = "Select your seat",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Spacing.sm))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeatLegendItem("Available", SeatStatus.AVAILABLE)
                    SeatLegendItem("Premium", SeatStatus.PREMIUM)
                    SeatLegendItem("Selected", SeatStatus.SELECTED)
                    SeatLegendItem("Occupied", SeatStatus.OCCUPIED)
                }

                if (localError != null) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    ErrorBanner(
                        message = localError ?: "Select a seat to continue.",
                        onDismiss = { localError = null }
                    )
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    ErrorBanner(
                        message = uiState.error ?: "Unable to reserve seat.",
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 3. Airplane Cabin Container - Adding horizontal scroll for seats
            if (seatMap.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.gutter)
                        .padding(bottom = Spacing.xxl),
                    shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 1.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = Spacing.xl, horizontal = Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.xl))

                            rows.forEach { rowNum ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                        columns.take(aisleIndex).forEach { col ->
                                            val code = "$rowNum$col"
                                            val seat = seatMap.firstOrNull { it.seatCode == code }
                                            SeatGridItem(
                                                seatCode = code,
                                                status = when {
                                                    code == selectedSeatCode -> SeatStatus.SELECTED
                                                    seat?.isOccupied == true -> SeatStatus.OCCUPIED
                                                    else -> SeatStatus.AVAILABLE
                                                },
                                                onClick = { if (seat?.isOccupied != true) selectedSeatCode = code }
                                            )
                                        }
                                    }

                                    Text(
                                        text = rowNum.toString(),
                                        modifier = Modifier.width(36.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                        columns.drop(aisleIndex).forEach { col ->
                                            val code = "$rowNum$col"
                                            val seat = seatMap.firstOrNull { it.seatCode == code }
                                            SeatGridItem(
                                                seatCode = code,
                                                status = when {
                                                    code == selectedSeatCode -> SeatStatus.SELECTED
                                                    seat?.isOccupied == true -> SeatStatus.OCCUPIED
                                                    else -> SeatStatus.AVAILABLE
                                                },
                                                onClick = { if (seat?.isOccupied != true) selectedSeatCode = code }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(Spacing.sm))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeatConfirmationBar(
    seatCode: String,
    seatType: String,
    fee: String,
    onConfirm: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = Spacing.gutter, vertical = Spacing.md)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SELECTED SEAT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = seatCode,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF051849)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = seatType,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "FEE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = fee,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (fee == "Included") MaterialTheme.colorScheme.primary else Color(0xFFD4AF37)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            ConfirmButton(
                text = "Confirm Seat",
                onClick = onConfirm
            )
        }
    }
}
