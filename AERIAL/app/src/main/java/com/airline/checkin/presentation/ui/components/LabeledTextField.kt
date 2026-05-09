package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airline.checkin.presentation.ui.theme.ShapeInput
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun LabeledTextField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        error: String? = null,
        enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeInput,
                isError = error != null,
                textStyle = MaterialTheme.typography.bodyLarge
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
            )
        }
    }
}
