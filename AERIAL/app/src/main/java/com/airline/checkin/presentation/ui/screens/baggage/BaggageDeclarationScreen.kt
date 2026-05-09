package com.airline.checkin.presentation.ui.screens.baggage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Luggage
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.PrimaryButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun BaggageDeclarationScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    var checkedBags by remember { mutableIntStateOf(1) }
    var cabinBags by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.gutter)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = "Baggage",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "Declare your luggage before boarding.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.mdPlus))

            BaggageCounter(
                label = "Checked Bags",
                subtitle = "Up to 23 kg each",
                count = checkedBags,
                onIncrement = { if (checkedBags < 3) checkedBags++ },
                onDecrement = { if (checkedBags > 0) checkedBags-- }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            BaggageCounter(
                label = "Cabin Bags",
                subtitle = "Up to 7 kg each",
                count = cabinBags,
                onIncrement = { if (cabinBags < 2) cabinBags++ },
                onDecrement = { if (cabinBags > 0) cabinBags-- }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(Spacing.mdPlus))

            PrimaryButton(text = "Continue", onClick = onContinue)

            Spacer(modifier = Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun BaggageCounter(
    label: String,
    subtitle: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    OutlinedCard(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Luggage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDecrement) {
                Icon(Icons.Rounded.Remove, contentDescription = "Remove")
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onIncrement) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    }
}
