package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.theme.ShapeButton

@Composable
fun PrimaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
) {
    Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.fillMaxWidth().heightIn(min = 44.dp),
            shape = ShapeButton,
            colors =
                    ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                    )
    ) { Text(text, style = MaterialTheme.typography.bodyLarge) }
}

@Composable
fun SecondaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
) {
    OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.fillMaxWidth().heightIn(min = 44.dp),
            shape = ShapeButton,
            colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
            )
    ) { Text(text, style = MaterialTheme.typography.bodyLarge) }
}

@Composable
fun GoldButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
) {
    Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.fillMaxWidth().heightIn(min = 44.dp),
            shape = ShapeButton,
            colors =
                    ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
    ) { Text(text, style = MaterialTheme.typography.bodyLarge) }
}
