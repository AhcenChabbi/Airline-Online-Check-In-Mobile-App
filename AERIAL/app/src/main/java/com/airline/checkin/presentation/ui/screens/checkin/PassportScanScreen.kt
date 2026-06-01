package com.airline.checkin.presentation.ui.screens.checkin

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airline.checkin.core.utils.OcrHelper
import com.airline.checkin.domain.model.PassportScanData
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel

@Composable
fun PassportScanScreen(
    onScanComplete: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    var showCamera by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        showCamera = granted
        errorMessage = if (granted) null else "Camera permission is required to scan a passport."
    }

    if (showCamera) {
        PassportCameraScanner(
            modifier = Modifier.fillMaxSize(),
            errorMessage = errorMessage,
            onErrorDismissed = { errorMessage = null },
            onCancel = { showCamera = false },
            onSkip = onSkip,
            onCaptured = { scanData ->
                if (scanData == null) {
                    errorMessage = "Could not read the passport MRZ. Try again or enter the details manually."
                    return@PassportCameraScanner
                }

                viewModel.applyPassportScan(scanData)
                onScanComplete()
            }
        )
    } else {
        ScanPromptScreen(
            onStartScan = {
                val permission = Manifest.permission.CAMERA
                val granted = ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
                if (granted) {
                    showCamera = true
                } else {
                    permissionLauncher.launch(permission)
                }
            },
            onSkip = onSkip,
            onBack = onBack
        )
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

@Composable
fun ScanPromptScreen(
    onStartScan: () -> Unit,
    onSkip: () -> Unit,
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
                    text = "Position your passport's photo page within the frame.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 2. Placeholder Scanner Visual
            PassportScannerView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.gutter),
                onClick = onStartScan
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Instructions Card
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
                    InstructionItem(
                        icon = Icons.Rounded.CheckCircle,
                        text = "Show the photo page with MRZ"
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
                    icon = Icons.Rounded.CameraAlt,
                    onClick = onStartScan
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Skip & Enter Manually",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
