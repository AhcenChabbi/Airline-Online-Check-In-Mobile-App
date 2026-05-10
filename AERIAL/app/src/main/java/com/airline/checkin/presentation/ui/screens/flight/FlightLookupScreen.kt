package com.airline.checkin.presentation.ui.screens.flight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplaneTicket
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airline.checkin.R
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun FlightLookupScreen(
    onFlightSelected: () -> Unit,
    onLogout: () -> Unit,
    flightResult: Flight? = null,
    bookedPassengerName: String = "",
    onNavigateToNotifications: () -> Unit = {}
) {
    var bookingRef by remember { mutableStateOf("") }
    var lastName   by remember { mutableStateOf("") }
    var isSearchDone by remember { mutableStateOf(false) }

    val previewFlight = Flight(
        id              = "preview",
        flightNumber    = "AF1234",
        airlineCode     = "AF",
        airlineName     = "Air France",
        originIata      = "CDG",
        originCity      = "Paris",
        destinationIata = "ALG",
        destinationCity = "Algiers",
        departureTime   = "10:30 AM",
        arrivalTime     = "12:45 PM",
        date            = "Oct 24, 2023",
        aircraftType    = "A320",
        status          = "SCHEDULED"
    )
    val displayedFlight      = flightResult ?: previewFlight
    val displayedPassenger   = bookedPassengerName.ifBlank { lastName }

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = stringResource(R.string.company_name),
                onNotificationClick = onNavigateToNotifications
            )
        },
        containerColor = Color(0xFFF9FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            // 1. Page Header
            Text(
                text = stringResource(R.string.find_your_flight),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = stringResource(R.string.retrieve_booking_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 2. Search Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Spacing.mdPlus),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.mdPlus)) {
                    AeroSearchTextField(
                        label       = stringResource(R.string.booking_ref_pnr),
                        placeholder = stringResource(R.string.booking_ref_eg),
                        value       = bookingRef,
                        onValueChange = { bookingRef = it.uppercase() },
                        leadingIcon = Icons.Rounded.AirplaneTicket
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    AeroSearchTextField(
                        label       = stringResource(R.string.last_name),
                        placeholder = stringResource(R.string.last_name_desc),
                        value       = lastName,
                        onValueChange = { lastName = it },
                        leadingIcon = Icons.Rounded.PersonOutline
                    )

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
                    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
                    SearchFlightButton(
                        onClick = { 
                            isSearchDone = true
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        enabled = bookingRef.isNotBlank() && lastName.isNotBlank()
                    )
                }
            }

            // 3. Search Results or Empty State
            if (isSearchDone || flightResult != null) {
                Spacer(modifier = Modifier.height(Spacing.xxl))

                Text(
                    text = stringResource(R.string.search_result),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                FlightResultCard(
                    flight              = displayedFlight,
                    bookingReference    = bookingRef,
                    bookedPassengerName = displayedPassenger,
                    onStartCheckIn      = onFlightSelected
                )
            } else {
                Spacer(modifier = Modifier.height(Spacing.lg))
                

                // Helpful Tips Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                ) {
                    Column(modifier = Modifier.padding(Spacing.mdPlus)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = "Pro Tip",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "You can find your Booking Reference (PNR) in your confirmation email or on your ticket.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

