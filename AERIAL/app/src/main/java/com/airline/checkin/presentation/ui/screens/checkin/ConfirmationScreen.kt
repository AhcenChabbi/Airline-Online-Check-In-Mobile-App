package com.airline.checkin.presentation.ui.screens.checkin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.PrimaryButton
import com.airline.checkin.presentation.ui.components.SuccessBanner
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun ConfirmationScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { AirlineTopBar(companyName = "AERIAL", onNotificationClick = {}) }) {
            innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.gutter)) {
            SuccessBanner(title = "Check-in Complete!", subtitle = "Your boarding pass is ready")
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(text = "View Boarding Pass", onClick = onContinue)
        }
    }
}
