package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier.fillMaxWidth().padding(vertical = Spacing.md)
    )
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
    }
}

@Composable
fun FlightChip(label: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.SuggestionChip(
            onClick = {},
            label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
            modifier = modifier,
            shape = com.airline.checkin.presentation.ui.theme.ShapeChip,
            colors =
                    androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
    )
}
