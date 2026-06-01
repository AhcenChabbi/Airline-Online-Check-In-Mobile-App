package com.airline.checkin.presentation.ui.screens.boarding

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.BoardingPassViewModel
import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import java.io.IOException

@Composable
fun BoardingPassScreen(
    onBack: () -> Unit,
    viewModel: BoardingPassViewModel = hiltViewModel(),
    checkInViewModel: CheckInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkInState by checkInViewModel.uiState.collectAsStateWithLifecycle()
    val checkInId = checkInState.checkIn?.id

    LaunchedEffect(checkInId) {
        if (checkInId != null) {
            viewModel.loadBoardingPass(checkInId)
        }
    }

    val boardingPass = uiState.boardingPass
    val passengerName = listOf(
        boardingPass?.offlinePayload?.passenger?.firstName,
        boardingPass?.offlinePayload?.passenger?.lastName
    ).filter { !it.isNullOrBlank() }.joinToString(" ").ifBlank {
        checkInState.passenger?.name.orEmpty()
    }
    val flightNumber = boardingPass?.offlinePayload?.flight?.flightNumber ?: checkInState.flight?.flightNumber.orEmpty()
    val from = boardingPass?.offlinePayload?.flight?.origin ?: checkInState.flight?.originIata.orEmpty()
    val to = boardingPass?.offlinePayload?.flight?.destination ?: checkInState.flight?.destinationIata.orEmpty()
    val date = checkInState.flight?.date.orEmpty()
    val boardingTime = checkInState.flight?.departureTime.orEmpty()
    val seatCode = boardingPass?.offlinePayload?.seat?.seatCode ?: checkInState.selectedSeat?.seatCode.orEmpty()
    val bookingRef = checkInState.bookingReference.ifBlank { checkInState.checkIn?.bookingId.orEmpty() }
    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {}
            )
        },
        containerColor = Color(0xFFF9FAFC),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
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
                text = "Your Boarding Pass",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "Present this digital pass at the boarding gate.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 2. The Boarding Pass Card
            BoardingPassCard(
                passengerName = passengerName,
                flightNumber = flightNumber,
                from = from,
                fromCity = checkInState.flight?.originCity.orEmpty(),
                to = to,
                toCity = checkInState.flight?.destinationCity.orEmpty(),
                date = date,
                seat = seatCode,
                boardingTime = boardingTime,
                bookingRef = bookingRef,
                qrCodeUrl = boardingPass?.qrCodeUrl,
                qrCodeData = boardingPass?.qrCodeData
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 3. Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        if (checkInId != null) {
                            viewModel.downloadPdf(checkInId) { bytes ->
                                val savedUri = saveBoardingPassPdf(context, checkInId, bytes)
                                if (savedUri != null) {
                                    Toast.makeText(
                                        context,
                                        "PDF saved to Downloads",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    openPdf(context, savedUri)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to save PDF",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PDF Pass", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(Spacing.md))
                
                ConfirmButton(
                    text = "Done",
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

private fun saveBoardingPassPdf(context: Context, checkInId: String, bytes: ByteArray): android.net.Uri? {
    val fileName = "boarding-pass-$checkInId.pdf"
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
    }

    return try {
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null
        resolver.openOutputStream(uri)?.use { output ->
            output.write(bytes)
            output.flush()
        } ?: return null
        uri
    } catch (e: IOException) {
        null
    }
}

private fun openPdf(context: Context, uri: android.net.Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(intent, "Open boarding pass PDF")
    if (chooser.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    }
}
