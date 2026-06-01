package com.airline.checkin.presentation.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import coil.compose.AsyncImage
import com.airline.checkin.presentation.ui.theme.Spacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BoardingPassCard(
    passengerName: String,
    flightNumber: String,
    from: String,
    fromCity: String,
    to: String,
    toCity: String,
    date: String,
    seat: String,
    boardingTime: String,
    bookingRef: String,
    qrCodeUrl: String? = null,
    qrCodeData: String? = null,
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
                    InfoColumn("DATE", formatDate(date), Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoColumn("SEAT", seat, Modifier.weight(1f))
                    InfoColumn("BOARDING", formatTime(boardingTime), Modifier.weight(1f))
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
                val qrBitmap = remember(qrCodeData) {
                    val payload = qrCodeData?.takeIf { it.isNotBlank() }
                    payload?.let { runCatching { generateQrBitmap(it) }.getOrNull() }
                }

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!qrCodeUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = qrCodeUrl,
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.QrCodeScanner,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(120.dp),
                            tint = Color(0xFF051849)
                        )
                    }
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

private fun generateQrBitmap(data: String, size: Int = 512): Bitmap {
    val matrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) return "—"
    return runCatching {
        val instant = Instant.parse(value)
        instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
    }.getOrNull() ?: value
}

private fun formatTime(value: String?): String {
    if (value.isNullOrBlank()) return "—"
    return runCatching {
        val instant = Instant.parse(value)
        instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrNull() ?: value
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
