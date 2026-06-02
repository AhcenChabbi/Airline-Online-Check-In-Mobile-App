package com.airline.checkin.presentation.ui.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airline.checkin.domain.model.PassportScanData

@Composable
fun DetailsReviewScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val passenger = uiState.passenger
    val flight = uiState.flight
    val checkInId = uiState.checkIn?.id
    var pendingContinue by remember { mutableStateOf(false) }
    var nameValidationError by remember { mutableStateOf<String?>(null) }

    // State for editable fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var passportNumber by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var passportExpiry by remember { mutableStateOf("") }

    LaunchedEffect(passenger) {
        firstName = passenger?.firstName.orEmpty()
        lastName = passenger?.lastName.orEmpty()
        passportNumber = passenger?.passportNumber.orEmpty()
        nationality = passenger?.nationality.orEmpty()
        dob = passenger?.dateOfBirth.orEmpty()
        passportExpiry = passenger?.passportExpiry.toApiDateString()
    }

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
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            val errorToShow = nameValidationError ?: uiState.error
            if (errorToShow != null) {
                ErrorBanner(
                    message = errorToShow,
                    onDismiss = {
                        nameValidationError = null
                        viewModel.clearError()
                    }
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // 1. Progress Bar
            StepProgressBar(currentStep = 2, totalSteps = 5)

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Review Details",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // 2. Flight Summary Card (Updated to use Flight model fields)
            FlightInfoCard(
                flightNumber = flight?.flightNumber.orEmpty(),
                originCode = flight?.originIata.orEmpty(),
                originCity = flight?.originCity.orEmpty(),
                destinationCode = flight?.destinationIata.orEmpty(),
                destinationCity = flight?.destinationCity.orEmpty()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Passenger Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Spacing.mdPlus),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(Spacing.gutter)) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Passenger Info",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    // Editable Fields based on Passenger model
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                            PassengerEditableField(
                                label = "First Name",
                                value = firstName,
                                onValueChange = { firstName = it },
                                modifier = Modifier.weight(1f)
                            )
                            PassengerEditableField(
                                label = "Last Name",
                                value = lastName,
                                onValueChange = { lastName = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        PassengerEditableField(
                            label = "Passport Number",
                            value = passportNumber,
                            onValueChange = { passportNumber = it }
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                            PassengerEditableField(
                                label = "Nationality",
                                value = nationality,
                                onValueChange = { nationality = it },
                                modifier = Modifier.weight(1f)
                            )
                            PassengerEditableField(
                                label = "Date of Birth",
                                value = dob,
                                onValueChange = { dob = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        PassengerEditableField(
                            label = "Passport Expiry Date",
                            value = passportExpiry,
                            onValueChange = { passportExpiry = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 4. Confirm Button
            ConfirmButton(
                text = "Confirm Details",
                onClick = {
                    val originalPassenger = uiState.booking?.passengers?.find { it.id == passenger?.id }
                    val originalFirstName = originalPassenger?.firstName.orEmpty().trim()
                    val originalLastName = originalPassenger?.lastName.orEmpty().trim()

                    if (!firstName.trim().equals(originalFirstName, ignoreCase = true) ||
                        !lastName.trim().equals(originalLastName, ignoreCase = true)) {
                        nameValidationError = "Passenger name does not match the booking details. Please verify and correct the first name and last name."
                    } else {
                        nameValidationError = null
                        if (checkInId != null) {
                            pendingContinue = true
                            viewModel.submitPassportAndConfirm(
                                checkInId,
                                PassportScanData(
                                    passportNumber = passportNumber,
                                    passportExpiry = passportExpiry.toApiDateString(),
                                    passportMrz = passenger?.passportMrz,
                                    passportScanUrl = passenger?.passportScanUrl
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.padding(bottom = Spacing.lg)
            )
        }
    }
}

private fun String?.toApiDateString(): String =
    this?.takeIf { it.isNotBlank() }?.let { value ->
        if (value.length >= 10) value.substring(0, 10) else value
    }.orEmpty()
