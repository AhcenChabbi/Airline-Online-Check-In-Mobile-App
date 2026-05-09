package com.airline.checkin.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AirlightColorScheme =
        lightColorScheme(
                primary = Primary,
                onPrimary = OnPrimary,
                primaryContainer = PrimaryContainer,
                onPrimaryContainer = OnPrimaryContainer,
                inversePrimary = InversePrimary,
                secondary = Secondary,
                onSecondary = OnSecondary,
                secondaryContainer = SecondaryContainer,
                onSecondaryContainer = OnSecondaryContainer,
                tertiary = Tertiary,
                onTertiary = OnTertiary,
                tertiaryContainer = TertiaryContainer,
                onTertiaryContainer = OnTertiaryContainer,
                error = Error,
                onError = OnError,
                errorContainer = ErrorContainer,
                onErrorContainer = OnErrorContainer,
                background = Background,
                onBackground = OnBackground,
                surface = Surface,
                onSurface = OnSurface,
                onSurfaceVariant = OnSurfaceVariant,
                surfaceVariant = SurfaceVariant,
                inverseSurface = InverseSurface,
                inverseOnSurface = InverseOnSurface,
                outline = Outline,
                outlineVariant = OutlineVariant,
                surfaceTint = SurfaceTint
        )

@Composable
fun AirlineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = AirlightColorScheme,
            typography = AirlineTypography,
            shapes = AirlineShapes,
            content = content
    )
}
