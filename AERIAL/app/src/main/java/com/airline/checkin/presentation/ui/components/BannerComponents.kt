package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun ErrorBanner(
        message: String,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
    Column(
            modifier =
                    modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(Spacing.md)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Dismiss error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        if (onRetry != null) {
            SecondaryButton(
                    text = "Retry",
                    onClick = onRetry,
                    modifier = Modifier.padding(top = Spacing.sm)
            )
        }
    }
}

@Composable
fun SuccessBanner(title: String, subtitle: String = "", modifier: Modifier = Modifier) {
    Column(
            modifier =
                    modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(Spacing.md)
    ) {
        Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        if (subtitle.isNotEmpty()) {
            Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Column(
            modifier =
                    modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(Spacing.md)
    ) {
        Text(
                text = "You are offline",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
        )
    }
}
