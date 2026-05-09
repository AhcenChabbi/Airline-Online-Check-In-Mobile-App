package com.airline.checkin.presentation.ui.screens.special

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.PrimaryButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun SpecialRequestsScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    var wheelchair by remember { mutableStateOf(false) }
    var vegetarianMeal by remember { mutableStateOf(false) }
    var infantSeat by remember { mutableStateOf(false) }
    var extraLegroom by remember { mutableStateOf(false) }
    var unaccompaniedMinor by remember { mutableStateOf(false) }

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
                text = "Special Requests",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "Let us know your needs so we can assist you better.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.mdPlus))

            SpecialRequestItem(
                label = "Wheelchair assistance",
                checked = wheelchair,
                onCheckedChange = { wheelchair = it }
            )
            SpecialRequestItem(
                label = "Vegetarian meal",
                checked = vegetarianMeal,
                onCheckedChange = { vegetarianMeal = it }
            )
            SpecialRequestItem(
                label = "Infant seat / bassinet",
                checked = infantSeat,
                onCheckedChange = { infantSeat = it }
            )
            SpecialRequestItem(
                label = "Extra legroom (subject to availability)",
                checked = extraLegroom,
                onCheckedChange = { extraLegroom = it }
            )
            SpecialRequestItem(
                label = "Unaccompanied minor service",
                checked = unaccompaniedMinor,
                onCheckedChange = { unaccompaniedMinor = it }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(Spacing.mdPlus))

            PrimaryButton(text = "Continue", onClick = onContinue)

            Spacer(modifier = Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun SpecialRequestItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
