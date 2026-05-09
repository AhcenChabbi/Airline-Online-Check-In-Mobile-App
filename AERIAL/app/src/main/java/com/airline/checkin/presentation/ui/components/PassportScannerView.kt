package com.airline.checkin.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CenterFocusWeak
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airline.checkin.R
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold

@Composable
fun PassportScannerView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        // Main Image Background
        Image(
            painter = painterResource(id = R.drawable.passport_placeholder),
            contentDescription = "Passport Scan Viewfinder",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Yellow Frame Overlay
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .aspectRatio(1.4f)
                .border(2.dp, Gold, RoundedCornerShape(2.dp))
        ) {
            // Horizontal line across the frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Gold)
                    .align(Alignment.Center)
            )
        }

        // "Align MRZ within frame" Pill
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 2.dp
        ) {
            Text(
                text = "Align MRZ within frame",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = Color(0xFF051849)
            )
        }
    }
}
