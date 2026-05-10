package com.airline.checkin.presentation.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.R
import com.airline.checkin.domain.model.Notification
import com.airline.checkin.domain.model.NotificationStatus
import com.airline.checkin.domain.model.NotificationType
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.theme.Spacing
import java.util.Date

@Composable
fun NotificationScreen(onBack: () -> Unit) {
    // Mock Data
    val mockNotifications = listOf(
        Notification(
            id = "1", userId = "u1", bookingId = "b1",
            type = NotificationType.GATE_CHANGE,
            channel = com.airline.checkin.domain.model.NotificationChannel.PUSH,
            status = NotificationStatus.SENT,
            title = "Gate Change",
            body = "Your flight AF1234 now departs from Gate C12.",
            sentAt = Date(), readAt = null
        ),
        Notification(
            id = "2", userId = "u1", bookingId = "b1",
            type = NotificationType.CHECKIN_CONFIRMED,
            channel = com.airline.checkin.domain.model.NotificationChannel.PUSH,
            status = NotificationStatus.READ,
            title = "Check-in Successful",
            body = "You are successfully checked in for your flight to Algiers.",
            sentAt = Date(), readAt = Date()
        ),
        Notification(
            id = "3", userId = "u1", bookingId = "b2",
            type = NotificationType.FLIGHT_DELAY,
            channel = com.airline.checkin.domain.model.NotificationChannel.PUSH,
            status = NotificationStatus.SENT,
            title = "Flight Delayed",
            body = "Flight BA0011 is delayed by 45 minutes.",
            sentAt = Date(), readAt = null
        )
    )

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "Notifications",
                onNotificationClick = {},
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (mockNotifications.isEmpty()) {
            EmptyNotifications(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(Spacing.gutter),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(mockNotifications) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification) {
    val isRead = notification.status == NotificationStatus.READ
    val (icon, iconColor) = getNotificationStyle(notification.type)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = if (isRead) 0.dp else 1.dp,
        border = if (isRead) androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.mdPlus)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isRead) FontWeight.SemiBold else FontWeight.Bold,
                        color = if (isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (!isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = "Just now", // In real app: format notification.sentAt
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun EmptyNotifications(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.NotificationsOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = "No notifications yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun getNotificationStyle(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.GATE_CHANGE -> Icons.Rounded.DoorSliding to Color(0xFFF59E0B)
        NotificationType.FLIGHT_DELAY -> Icons.Rounded.Schedule to Color(0xFFEF4444)
        NotificationType.FLIGHT_CANCELLED -> Icons.Rounded.Cancel to Color(0xFFB91C1C)
        NotificationType.CHECKIN_CONFIRMED -> Icons.Rounded.CheckCircle to Color(0xFF10B981)
        NotificationType.BOARDING_PASS_READY -> Icons.Rounded.ConfirmationNumber to Color(0xFF3B82F6)
        NotificationType.REMINDER -> Icons.Rounded.NotificationsActive to Color(0xFF6366F1)
    }
}
