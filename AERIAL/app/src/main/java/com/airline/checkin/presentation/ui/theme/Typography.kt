package com.airline.checkin.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.airline.checkin.R

private val HeadlineFamily = FontFamily(
    Font(R.font.plus_jakarta_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_semibold, FontWeight.SemiBold)
)

private val BodyFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium)
)

val AirlineTypography =
        Typography(
                displayLarge =
                        TextStyle(
                                fontFamily = HeadlineFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp,
                                lineHeight = 52.8.sp
                        ),
                headlineMedium =
                        TextStyle(
                                fontFamily = HeadlineFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp,
                                lineHeight = 31.2.sp
                        ),
                titleSmall =
                        TextStyle(
                                fontFamily = HeadlineFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                lineHeight = 25.2.sp
                        ),
                bodyLarge =
                        TextStyle(
                                fontFamily = BodyFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                lineHeight = 25.6.sp
                        ),
                bodyMedium =
                        TextStyle(
                                fontFamily = BodyFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 21.sp
                        ),
                labelSmall =
                        TextStyle(
                                fontFamily = BodyFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                lineHeight = 14.4.sp,
                                letterSpacing = 0.05.sp
                        )
        )
