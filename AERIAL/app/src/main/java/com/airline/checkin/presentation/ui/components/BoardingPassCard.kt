package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun BoardingPassCard(
    passengerName: String,
    flightNumber: String,
    from: String,
    fromCity: String,
    to: String,
    toCity: String,
    date: String,
    gate: String,
    seat: String,
    boardingTime: String,
    bookingRef: String,
    qrCodeData: String = "AERIAL-PASS-123",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Top Section (Navy Header)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF051849))
                    .padding(Spacing.mdPlus)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(from, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Black)
                        Text(fromCity, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.AirplanemodeActive, contentDescription = null, tint = Color(0xFFD4AF37))
                        Text(flightNumber, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(to, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Black)
                        Text(toCity, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    }
                }
            }

            // 2. Middle Section (Details)
            Column(modifier = Modifier.padding(Spacing.mdPlus)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoColumn("PASSENGER", passengerName, Modifier.weight(1.5f))
                    InfoColumn("DATE", date, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoColumn("GATE", gate, Modifier.weight(1f))
                    InfoColumn("SEAT", seat, Modifier.weight(1f))
                    InfoColumn("BOARDING", boardingTime, Modifier.weight(1f))
                }
            }

            // 3. Perforated Line
            DashedDivider()

            // 4. Bottom Section (QR Code)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.QrCode2,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(120.dp),
                        tint = Color(0xFF051849)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Text(
                    text = "BOOKING REF: $bookingRef",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF051849))
    }
}

@Composable
private fun DashedDivider() {
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp).padding(horizontal = Spacing.mdPlus)) {
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}
