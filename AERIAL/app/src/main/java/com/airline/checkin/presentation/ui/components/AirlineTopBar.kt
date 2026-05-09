package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.theme.AirlineTheme
import com.airline.checkin.presentation.ui.theme.Gold
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.R

@Composable
fun AirlineTopBar(
        companyName: String,
        onNotificationClick: () -> Unit,
        modifier: Modifier = Modifier,
        hasUnreadNotifications: Boolean = true,
        onBackClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = Spacing.gutter, vertical = Spacing.smPlus),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                } else {
                    Box(
                            modifier =
                                    Modifier.size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                    ) {
                        Icon(
                                imageVector = Icons.Rounded.FlightTakeoff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
                Text(
                        text = companyName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onNotificationClick) {
                Box {
                    Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = stringResource(R.string.notifications),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (hasUnreadNotifications) {
                        Box(
                                modifier =
                                        Modifier.align(Alignment.TopEnd)
                                                .padding(top = 2.dp, end = 2.dp)
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Gold) // Using the theme's Gold color
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
    }
}

@Preview
@Composable
private fun AirlineTopBarPreview() {
    AirlineTheme { AirlineTopBar(companyName = "AERIAL", onNotificationClick = {}) }
}
