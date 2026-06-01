package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airline.checkin.core.utils.QRCodeUtils

@Composable
fun QRCodeView(
        data: String,
        size: Dp,
        contentDesc: String,
        modifier: Modifier = Modifier
) {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val bitmap = remember(data, sizePx) { QRCodeUtils.generateQrBitmap(data, sizePx) }

    Box(
            modifier =
                    modifier
                            .size(size)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = contentDesc
            )
        } else {
            Icon(
                    imageVector = Icons.Rounded.QrCode2,
                    contentDescription = contentDesc,
                    tint = Color(0xFF051849)
            )
        }
    }
}
