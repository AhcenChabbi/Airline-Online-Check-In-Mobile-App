package com.airline.checkin.presentation.ui.screens.flight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplaneTicket
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.airline.checkin.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.AeroSearchTextField
import com.airline.checkin.presentation.ui.components.FlightResultCard
import com.airline.checkin.presentation.ui.components.SearchFlightButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun FlightLookupScreen(
    onFlightSelected: () -> Unit,
    onLogout: () -> Unit,
    flightResult: Flight? = null,
    bookedPassengerName: String = ""
) {
    var bookingRef by remember { mutableStateOf("") }
    var lastName   by remember { mutableStateOf("") }

    var isSearchDone by remember { mutableStateOf(false) }

    val previewFlight = Flight(
        id              = "preview",
        number          = "SK-1042",
        airline         = "Skyward Premium",
        origin          = "London",
        originCode      = "LHR",
        destination     = "New York",
        destinationCode = "JFK",
        departureTime   = "08:45 AM",
        arrivalTime     = "11:05 AM",
        date            = "Oct 24, 2023",
        gate            = "B14",
        status          = "On Time"
    )
    val displayedFlight      = flightResult ?: previewFlight
    val displayedPassenger   = bookedPassengerName.ifBlank { lastName }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AirlineTopBar(
                companyName = stringResource(R.string.company_name),
                onNotificationClick = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.gutter)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                // ── Page title
                Text(
                    text = stringResource(R.string.find_your_flight),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    lineHeight = MaterialTheme.typography.displaySmall.lineHeight * 0.9f
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = stringResource(R.string.retrieve_booking_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // ── Search form card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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

                        Spacer(modifier = Modifier.height(Spacing.mdPlus))

                        AeroSearchTextField(
                            label       = stringResource(R.string.last_name),
                            placeholder = stringResource(R.string.last_name_desc),
                            value       = lastName,
                            onValueChange = { lastName = it },
                            leadingIcon = Icons.Rounded.PersonOutline
                        )

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        SearchFlightButton(
                            onClick = { isSearchDone = true },
                            enabled = bookingRef.isNotBlank() && lastName.isNotBlank()
                        )

                        Spacer(modifier = Modifier.height(Spacing.mdPlus))

                        Text(
                            text = stringResource(R.string.results_below),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ── Search result
                if (isSearchDone || flightResult != null) {
                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Text(
                        text = stringResource(R.string.search_result),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    FlightResultCard(
                        flight              = displayedFlight,
                        bookingReference    = bookingRef,
                        bookedPassengerName = displayedPassenger,
                        onStartCheckIn      = onFlightSelected
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}
