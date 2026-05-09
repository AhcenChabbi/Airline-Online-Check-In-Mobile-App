package com.airline.checkin.presentation.ui.screens.checkin

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.R
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.PassportScannerView
import com.airline.checkin.presentation.ui.components.PrimaryButton
import com.airline.checkin.presentation.ui.components.StepProgressBar
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun PassportScanScreen(
    onScanComplete: () -> Unit,
    onBack: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFC))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AirlineTopBar(
                companyName = stringResource(R.string.company_name),
                onNotificationClick = {},
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.gutter)
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                // Segmented Progress Bar (StepProgressBar component)
                StepProgressBar(currentStep = 1, totalSteps = 6)

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = "Scan Passport",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primaryContainer
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // The Scanner component (Clickable area that transitions to camera)
                PassportScannerView(
                    modifier = Modifier.weight(1f),
                    onClick = onScanComplete
                )

                // Instructions Section
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                InstructionItem(
                    icon = Icons.Rounded.DocumentScanner,
                    text = "Make sure the photo page is facing up"
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                InstructionItem(
                    icon = Icons.Rounded.LightMode,
                    text = "Ensure good lighting"
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                InstructionItem(
                    icon = Icons.Rounded.PanTool,
                    text = "Hold still"
                )

                Spacer(modifier = Modifier.weight(1f))

                // Single Scan Button using Design System
                PrimaryButton(
                    text = "Scan",
                    onClick = onScanComplete,
                    modifier = Modifier.padding(bottom = Spacing.mdPlus)
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
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

