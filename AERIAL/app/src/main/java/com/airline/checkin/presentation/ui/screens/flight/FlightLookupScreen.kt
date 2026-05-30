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
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import com.airline.checkin.presentation.ui.viewmodels.FlightViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FlightLookupScreen(
    onFlightSelected: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    viewModel: FlightViewModel = hiltViewModel(),
    checkInViewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkInState by checkInViewModel.uiState.collectAsStateWithLifecycle()
    var bookingRef by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isSearchDone by remember { mutableStateOf(false) }
    var pendingSearch by remember { mutableStateOf(false) }
    var pendingStart by remember { mutableStateOf(false) }

    val bookingLookup = uiState.bookingLookup
    val displayedFlight = bookingLookup?.flight
    val primaryPassenger = bookingLookup?.passengers?.firstOrNull { it.isPrimary }
    val displayedPassenger = primaryPassenger?.name ?: lastName

    LaunchedEffect(uiState.isLoading, uiState.error, bookingLookup) {
        if (pendingSearch && !uiState.isLoading) {
            pendingSearch = false
            isSearchDone = true
        }
    }

    LaunchedEffect(checkInState.isLoading, checkInState.error, checkInState.checkIn) {
        if (pendingStart && !checkInState.isLoading) {
            if (checkInState.error == null && checkInState.checkIn != null) {
                onFlightSelected()
            }
            pendingStart = false
        }
    }

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
                            pendingSearch = true
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            viewModel.lookupBooking(bookingRef, lastName)
                        },
                        enabled = bookingRef.isNotBlank() && lastName.isNotBlank()
                    )
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                ErrorBanner(
                    message = uiState.error ?: "Lookup failed.",
                    onDismiss = { viewModel.clearError() }
                )
            }

            if (checkInState.error != null) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                ErrorBanner(
                    message = checkInState.error ?: "Check-in failed.",
                    onDismiss = { checkInViewModel.clearError() }
                )
            }

            // 3. Search Results or Empty State
            if (isSearchDone || bookingLookup != null) {
                Spacer(modifier = Modifier.height(Spacing.xxl))

                Text(
                    text = stringResource(R.string.search_result),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                if (displayedFlight != null && bookingLookup != null) {
                    FlightResultCard(
                        flight = displayedFlight,
                        bookingReference = bookingLookup.bookingReference.ifBlank { bookingRef },
                        bookedPassengerName = displayedPassenger,
                        onStartCheckIn = {
                            val passengerId = primaryPassenger?.id
                            if (passengerId != null) {
                                pendingStart = true
                                checkInViewModel.initiateCheckIn(bookingLookup.bookingId, passengerId)
                            }
                        }
                    )
                } else if (isSearchDone && uiState.error == null) {
                    Text(
                        text = "No booking found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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

