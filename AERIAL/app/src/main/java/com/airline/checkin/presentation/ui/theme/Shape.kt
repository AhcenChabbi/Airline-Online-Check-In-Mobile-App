package com.airline.checkin.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AirlineShapes =
        Shapes(
                extraSmall = RoundedCornerShape(4.dp),
                small = RoundedCornerShape(8.dp),
                medium = RoundedCornerShape(12.dp),
                large = RoundedCornerShape(16.dp),
                extraLarge = RoundedCornerShape(24.dp)
        )

val ShapePill = RoundedCornerShape(percent = 50)
val ShapeCard = RoundedCornerShape(16.dp)
val ShapeButton = RoundedCornerShape(8.dp)
val ShapeInput = RoundedCornerShape(8.dp)
val ShapeChip = RoundedCornerShape(8.dp)
