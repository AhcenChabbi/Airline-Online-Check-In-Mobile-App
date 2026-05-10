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
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val scanPosition by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp)) // Oval-style rounded corners
            .background(Color.Black)
            .border(2.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(40.dp))
            .clickable { onClick() }
    ) {
        // Main Image Background
        Image(
            painter = painterResource(id = R.drawable.image),
            contentDescription = "Passport Scan Viewfinder",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Large Focus Icon in the center (dimmed)
        Icon(
            imageVector = Icons.Rounded.CenterFocusWeak,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.Center),
            tint = Gold.copy(alpha = 0.2f)
        )

        // Yellow Frame Overlay
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .aspectRatio(1.4f)
                .border(2.dp, Gold, RoundedCornerShape(4.dp))
        ) {
            // Animated Scanning line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .offset(y = maxHeight * scanPosition)
                    .background(Gold)
                    .border(1.dp, Gold.copy(alpha = 0.5f))
            )
        }

        // "Align MRZ within frame" Pill
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.6f),
            shadowElevation = 4.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CenterFocusWeak,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Gold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scanning Document...",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}
