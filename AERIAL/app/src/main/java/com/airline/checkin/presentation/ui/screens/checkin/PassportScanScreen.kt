package com.airline.checkin.presentation.ui.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold

@Composable
fun PassportScanScreen(
    onScanComplete: () -> Unit,
    onBack: () -> Unit
) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Header Section (Consistent with Baggage/Seats)
            Column(modifier = Modifier.padding(horizontal = Spacing.gutter)) {
                Spacer(modifier = Modifier.height(Spacing.md))
                StepProgressBar(currentStep = 1, totalSteps = 5)
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Text(
                    text = "Scan Passport",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Text(
                    text = "Position your passport's photo page within the frame below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 2. The Scanner component
            PassportScannerView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.gutter),
                onClick = onScanComplete
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Instructions Card (Premium Style)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.gutter),
                shape = RoundedCornerShape(Spacing.mdPlus),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.mdPlus)
                ) {
                    Text(
                        text = "Tips for a better scan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    InstructionItem(
                        icon = Icons.Rounded.CheckCircle,
                        text = "Avoid glare and ensure good lighting"
                    )
                    InstructionItem(
                        icon = Icons.Rounded.CheckCircle,
                        text = "Hold your phone steady"
                    )
                }
            }

            // 4. Action Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.gutter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConfirmButton(
                    text = "Start Scanning",
                    icon = Icons.Rounded.CenterFocusWeak,
                    onClick = onScanComplete
                )
            }
        }
    }
}

@Composable
fun InstructionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
