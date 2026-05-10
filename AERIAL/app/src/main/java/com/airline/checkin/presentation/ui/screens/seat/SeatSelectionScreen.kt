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

@Composable
fun SeatSelectionScreen(onSeatConfirmed: () -> Unit, onBack: () -> Unit) {
    var selectedSeatCode by remember { mutableStateOf("12B") }
    
    val totalRows = 30
    val rows = (10..totalRows).toList()
    val columns = listOf("A", "B", "C", "D")
    val aisleIndex = 2
    
    val occupiedSeats = remember { setOf("10B", "10C", "11A", "11B", "12D", "15A", "15F") }
    val premiumRows = remember { setOf(10, 11, 12) }

    val selectedRow = selectedSeatCode.filter { it.isDigit() }.toIntOrNull() ?: 12
    val selectedCol = selectedSeatCode.filter { it.isLetter() }
    val isPremium = premiumRows.contains(selectedRow)
    
    val seatType = when (selectedCol) {
        "A", "F" -> "Window Seat"
        "B", "E" -> "Middle Seat"
        else -> "Aisle Seat"
    }
    
    val seatFee = if (isPremium) "$25.00" else "Included"

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
                seatCode = selectedSeatCode,
                seatType = seatType,
                fee = seatFee,
                onConfirm = onSeatConfirmed
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
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 3. Airplane Cabin Container - Adding horizontal scroll for seats
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.gutter)
                    .padding(bottom = Spacing.xxl),
                shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 1.dp
            ) {
                // Horizontal scroll for the entire seat grid to handle small screens
                // fillMaxWidth + Center alignment ensures it stays centered if it fits
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
                        // Top nose indicators
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(3) {
                                Box(modifier = Modifier.size(4.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.outlineVariant))
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        // Seat Grid
                        rows.forEach { rowNum ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Side
                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                    columns.take(aisleIndex).forEach { col ->
                                        val code = "$rowNum$col"
                                        SeatGridItem(
                                            seatCode = code,
                                            status = when {
                                                code == selectedSeatCode -> SeatStatus.SELECTED
                                                occupiedSeats.contains(code) -> SeatStatus.OCCUPIED
                                                premiumRows.contains(rowNum) -> SeatStatus.PREMIUM
                                                else -> SeatStatus.AVAILABLE
                                            },
                                            onClick = { selectedSeatCode = code }
                                        )
                                    }
                                }

                                // Row Number
                                Text(
                                    text = rowNum.toString(),
                                    modifier = Modifier.width(36.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                // Right Side
                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                    columns.drop(aisleIndex).forEach { col ->
                                        val code = "$rowNum$col"
                                        SeatGridItem(
                                            seatCode = code,
                                            status = when {
                                                code == selectedSeatCode -> SeatStatus.SELECTED
                                                occupiedSeats.contains(code) -> SeatStatus.OCCUPIED
                                                premiumRows.contains(rowNum) -> SeatStatus.PREMIUM
                                                else -> SeatStatus.AVAILABLE
                                            },
                                            onClick = { selectedSeatCode = code }
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
