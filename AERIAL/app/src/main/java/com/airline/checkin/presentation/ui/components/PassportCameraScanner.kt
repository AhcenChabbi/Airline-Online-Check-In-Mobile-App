package com.airline.checkin.presentation.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropFree
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow
import com.airline.checkin.core.utils.OcrHelper
import com.airline.checkin.domain.model.PassportScanData
import com.airline.checkin.presentation.ui.theme.Gold
import com.airline.checkin.presentation.ui.theme.Spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PassportCameraScanner(
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onErrorDismissed: () -> Unit,
    onCancel: () -> Unit,
    onSkip: () -> Unit,
    onCaptured: (PassportScanData?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var boundCameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
        if (uri == null || isProcessing) return@rememberLauncherForActivityResult
        isProcessing = true
        scope.launch {
            try {
                        Log.d("PassportCamera", "Gallery image selected: $uri")
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                }
                if (bitmap == null) {
                    Log.e("PassportCamera", "Failed to decode selected image: $uri")
                    onCaptured(null)
                    return@launch
                }

                val (scanData, raw) = withContext(Dispatchers.Default) {
                    OcrHelper.extractPassportDataWithRaw(bitmap)
                }
                Log.d("PassportCamera", "OCR raw text length: ${raw.length}")
                onCaptured(scanData)
            } catch (throwable: Throwable) {
                Log.e("PassportCamera", "Gallery OCR failed", throwable)
                onCaptured(null)
            } finally {
                isProcessing = false
            }
        }
    }

    LaunchedEffect(previewView) {
        val view = previewView ?: return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
                boundCameraProvider = cameraProvider
            } catch (throwable: Throwable) {
                Log.e("PassportCamera", "Unable to bind camera", throwable)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    DisposableEffect(Unit) {
        onDispose {
            boundCameraProvider?.unbindAll()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = Spacing.gutter, vertical = Spacing.md)) {
                AirlineTopBar(
                    companyName = "AERIAL",
                    onNotificationClick = {},
                    onBackClick = onCancel
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = "Scan Passport",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Center the photo page and keep the MRZ visible at the bottom.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = Spacing.gutter)
                .clip(RoundedCornerShape(Spacing.mdPlus))
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { previewView = it }
                },
                modifier = Modifier.fillMaxSize()
            )

            BoxWithConstraints(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.9f)
                    .height(220.dp)
                    .border(2.dp, Gold, RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.TopCenter)
                        .background(Gold.copy(alpha = 0.65f))
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Spacing.md),
                color = Color.Black.copy(alpha = 0.55f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "MRZ only",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (errorMessage != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(Spacing.md),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "You can try again or use manual entry.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedButton(onClick = onErrorDismissed, modifier = Modifier.padding(top = Spacing.sm)) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.gutter),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Button(
                onClick = {
                    if (isProcessing) return@Button
                    isProcessing = true
                    val outputFile = File.createTempFile("passport_scan_", ".jpg", context.cacheDir)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        scope.launch {
                                            try {
                                                Log.d("PassportCamera", "Image saved to: ${outputFile.absolutePath}")
                                                val bitmap = withContext(Dispatchers.IO) {
                                                    BitmapFactory.decodeFile(outputFile.absolutePath)
                                                }
                                                val (scanData, raw) = withContext(Dispatchers.Default) {
                                                    OcrHelper.extractPassportDataWithRaw(bitmap)
                                                }
                                                Log.d("PassportCamera", "OCR raw text length: ${raw.length}")
                                                onCaptured(scanData)
                                            } catch (throwable: Throwable) {
                                                Log.e("PassportCamera", "OCR failed", throwable)
                                                onCaptured(null)
                                            } finally {
                                                isProcessing = false
                                            }
                                        }
                                    }

                            override fun onError(exc: ImageCaptureException) {
                                Log.e("PassportCamera", "Capture failed", exc)
                                isProcessing = false
                                onCaptured(null)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(imageVector = Icons.Rounded.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(if (isProcessing) "Scanning..." else "Capture Passport")
            }

            FilledTonalButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Use manual entry")
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isProcessing
            ) {
                Icon(imageVector = Icons.Rounded.CropFree, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Choose Image")
            }
        }

        // Debug dialog removed; OCR results are delivered via `onCaptured`
    }
}