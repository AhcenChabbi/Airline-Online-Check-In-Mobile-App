package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BoardingPassCard(
    passengerName: String,
    flightNumber: String,
    from: String,
    to: String,
    date: String,
    gate: String,
    seat: String,
    boardingTime: String,
    bookingRef: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = passengerName, style = MaterialTheme.typography.titleSmall)
            Text(text = "Flight $flightNumber", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$from -> $to",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(text = date, style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "Gate $gate  Seat $seat", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Boarding $boardingTime  Ref $bookingRef",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
