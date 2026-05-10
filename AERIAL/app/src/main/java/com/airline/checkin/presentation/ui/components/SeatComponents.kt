package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold

enum class SeatStatus {
    AVAILABLE,
    PREMIUM,
    OCCUPIED,
    SELECTED
}

@Composable
fun SeatLegendItem(
    label: String,
    status: SeatStatus
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(
                    1.dp,
                    when (status) {
                        SeatStatus.AVAILABLE -> MaterialTheme.colorScheme.outlineVariant
                        SeatStatus.PREMIUM -> Gold
                        else -> Color.Transparent
                    },
                    RoundedCornerShape(4.dp)
                )
                .background(
                    when (status) {
                        SeatStatus.OCCUPIED -> MaterialTheme.colorScheme.surfaceContainerHigh
                        SeatStatus.SELECTED -> MaterialTheme.colorScheme.primary
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (status == SeatStatus.OCCUPIED) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SeatGridItem(
    seatCode: String,
    status: SeatStatus,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when (status) {
                    SeatStatus.SELECTED -> MaterialTheme.colorScheme.primary
                    SeatStatus.OCCUPIED -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = 1.dp,
                color = when (status) {
                    SeatStatus.SELECTED -> MaterialTheme.colorScheme.primary
                    SeatStatus.PREMIUM -> Gold
                    SeatStatus.OCCUPIED -> Color.Transparent
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = status != SeatStatus.OCCUPIED, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (status == SeatStatus.OCCUPIED) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
        } else {
            Text(
                text = seatCode,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = when (status) {
                    SeatStatus.SELECTED -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
