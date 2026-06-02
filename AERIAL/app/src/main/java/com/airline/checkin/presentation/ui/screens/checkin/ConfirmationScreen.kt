package com.airline.checkin.presentation.ui.screens.checkin

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.airline.checkin.MainActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConfirmationScreen(
    onContinue: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val passengerName = uiState.passenger?.name.orEmpty()
    val flightNumber = uiState.flight?.flightNumber.orEmpty()
    val origin = uiState.flight?.originIata.orEmpty()
    val destination = uiState.flight?.destinationIata.orEmpty()
    val originCity = uiState.flight?.originCity.orEmpty()
    val destinationCity = uiState.flight?.destinationCity.orEmpty()
    val seatCode = uiState.selectedSeat?.seatCode.orEmpty()
    val seatType = uiState.selectedSeat?.seatType.orEmpty()
    val baggageCount = uiState.baggage.sumOf { it.quantity }
    val pnr = uiState.bookingReference.ifBlank { uiState.checkIn?.bookingId.orEmpty() }
    var acknowledged by remember { mutableStateOf(false) }
    var pendingContinue by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            uiState.checkIn?.id?.let {
                pendingContinue = true
                viewModel.confirmCheckIn(it)
            }
        }
    )

    LaunchedEffect(uiState.isLoading, uiState.error, uiState.boardingPass) {
        if (pendingContinue && !uiState.isLoading) {
            if (uiState.error == null && uiState.boardingPass != null) {
                val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
                if (isPermissionGranted) {
                    sendLocalNotification(context, flightNumber, destination)
                }
                onContinue(uiState.boardingPass?.checkinId ?: uiState.checkIn?.id.orEmpty())
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

            if (uiState.error != null) {
                ErrorBanner(
                    message = uiState.error ?: "Unable to complete check-in.",
                    onDismiss = { viewModel.clearError() }
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // 1. Success Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Gold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Text(
                    text = "You're almost there!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF051849)
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = "Review your details below and complete your check-in to receive your boarding pass.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.md)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 2. Summary Card
            FinalSummaryCard(
                passengerName = passengerName,
                flightNumber = flightNumber,
                origin = origin,
                destination = destination,
                originCity = originCity,
                destinationCity = destinationCity,
                seat = seatCode,
                seatType = seatType,
                baggageCount = baggageCount,
                pnr = pnr
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Acknowledgment
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = acknowledged,
                    onCheckedChange = { acknowledged = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF051849))
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "I acknowledge that I have read and agree to the Conditions of Carriage and confirm my baggage does not contain any prohibited items.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 4. Action Section
            // Triggers the creation of a BoardingPass record in the backend
            ConfirmButton(
                text = "Complete Check-in",
                enabled = acknowledged,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val isPermissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!isPermissionGranted) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            uiState.checkIn?.id?.let {
                                pendingContinue = true
                                viewModel.confirmCheckIn(it)
                            }
                        }
                    } else {
                        uiState.checkIn?.id?.let {
                            pendingContinue = true
                            viewModel.confirmCheckIn(it)
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Text(
                text = "You will receive a digital boarding pass.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

private fun sendLocalNotification(
    context: Context,
    flightNumber: String,
    destination: String
) {
    val channelId = "aerial_channel"
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )
    
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Check-in Complete!")
        .setContentText("Your boarding pass for flight $flightNumber to $destination is ready.")
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "AERIAL Flight Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channels for flight check-in and boarding pass updates"
        }
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
}
