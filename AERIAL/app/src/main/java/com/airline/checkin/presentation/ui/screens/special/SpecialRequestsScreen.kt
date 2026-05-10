package com.airline.checkin.presentation.ui.screens.special

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessible
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing

/**
 * Maps to backend SpecialRequest model:
 * Category: DIETARY, ACCESSIBILITY, INFANT, PET, MEDICAL, OTHER
 * Detail: e.g. "VGML", "WCHR", "INFT", "PETC"
 */
@Composable
fun SpecialRequestsScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    // These states will eventually be part of a list of SpecialRequest objects
    var hasDietary by remember { mutableStateOf(false) }
    var hasAccessibility by remember { mutableStateOf(false) }
    var hasInfant by remember { mutableStateOf(false) }
    var hasPet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {},
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            // 1. Progress Bar
            StepProgressBar(currentStep = 4, totalSteps = 5)

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Special Requests",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "Let us know your needs so we can assist you better.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 2. Request Items (Mapped to RequestCategory)
            
            // DIETARY Category
            SpecialRequestCard(
                title = "Special meal",
                subtitle = "Dietary requirements (VGML, Halal...)",
                icon = Icons.Rounded.Restaurant,
                checked = hasDietary,
                onCheckedChange = { hasDietary = it }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // ACCESSIBILITY Category
            SpecialRequestCard(
                title = "Assistance",
                subtitle = "Mobility support (Wheelchair WCHR)",
                icon = Icons.Rounded.Accessible,
                checked = hasAccessibility,
                onCheckedChange = { hasAccessibility = it }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // INFANT Category
            SpecialRequestCard(
                title = "Infant onboard",
                subtitle = "Under 2 years old (INFT)",
                icon = Icons.Rounded.ChildCare,
                checked = hasInfant,
                onCheckedChange = { hasInfant = it }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // PET Category
            SpecialRequestCard(
                title = "Pet onboard",
                subtitle = "Cabin or cargo (PETC)",
                icon = Icons.Rounded.Pets,
                checked = hasPet,
                onCheckedChange = { hasPet = it }
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // 3. Complete Button
            ConfirmButton(
                text = "Complete Check-in",
                onClick = onContinue,
                modifier = Modifier.padding(bottom = Spacing.lg)
            )
        }
    }
}
