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
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airline.checkin.domain.model.SpecialRequest

/**
 * Maps to backend SpecialRequest model:
 * Category: DIETARY, ACCESSIBILITY, INFANT, PET, MEDICAL, OTHER
 * Detail: e.g. "VGML", "WCHR", "INFT", "PETC"
 */
@Composable
fun SpecialRequestsScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkInId = uiState.checkIn?.id
    // These states will eventually be part of a list of SpecialRequest objects
    var hasDietary by remember { mutableStateOf(false) }
    var hasAccessibility by remember { mutableStateOf(false) }
    var hasInfant by remember { mutableStateOf(false) }
    var hasPet by remember { mutableStateOf(false) }
    var pendingContinue by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading, uiState.error) {
        if (pendingContinue && !uiState.isLoading) {
            if (uiState.error == null) {
                onContinue()
            }
            pendingContinue = false
        }
    }

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

            if (uiState.error != null) {
                ErrorBanner(
                    message = uiState.error ?: "Unable to save special requests.",
                    onDismiss = { viewModel.clearError() }
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

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
                onClick = {
                    if (checkInId != null) {
                        val requests = buildList {
                            if (hasDietary) add(SpecialRequest(category = "DIETARY", detail = "VGML"))
                            if (hasAccessibility) add(SpecialRequest(category = "ACCESSIBILITY", detail = "WCHR"))
                            if (hasInfant) add(SpecialRequest(category = "INFANT", detail = "INFT"))
                            if (hasPet) add(SpecialRequest(category = "PET", detail = "PETC"))
                        }
                        pendingContinue = true
                        viewModel.submitSpecialRequests(checkInId, requests)
                    }
                },
                modifier = Modifier.padding(bottom = Spacing.lg)
            )
        }
    }
}
