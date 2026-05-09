# THEME_IMPL.md — Airline App · Compose Material 3 + MVVM
> **Agent directive**: Ce fichier contient le code Kotlin exact pour le thème. Copie-le tel quel, ne réinvente pas les couleurs, la typo ou les shapes. Toujours utiliser `MaterialTheme.colorScheme`, `MaterialTheme.typography`, `MaterialTheme.shapes`.

---

## Pattern : MVVM (pas MVI)

Ce projet utilise **MVVM standard** avec Jetpack Compose :

```
ViewModel  →  exposes  →  StateFlow<UiState>
Screen     →  collects →  uiState (read-only)
Screen     →  calls    →  viewModel.doSomething()  (méthodes directes)
```

### Template ViewModel
```kotlin
@HiltViewModel
class FlightViewModel @Inject constructor(
    private val getFlightByBooking: GetFlightByBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    fun loadFlight(bookingRef: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getFlightByBooking(bookingRef)
                .onSuccess { flight ->
                    _uiState.update { it.copy(flight = flight, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}

data class FlightUiState(
    val flight    : Flight?  = null,
    val isLoading : Boolean  = false,
    val error     : String?  = null
)
```

### Template Screen (MVVM)
```kotlin
@Composable
fun FlightDetailScreen(
    bookingRef : String,
    onContinue : () -> Unit,
    viewModel  : FlightViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Charger au démarrage
    LaunchedEffect(bookingRef) {
        viewModel.loadFlight(bookingRef)
    }

    FlightDetailContent(
        uiState    = uiState,
        onContinue = onContinue,
        onRetry    = { viewModel.loadFlight(bookingRef) }
    )
}

@Composable
private fun FlightDetailContent(
    uiState    : FlightUiState,
    onContinue : () -> Unit,
    onRetry    : () -> Unit
) {
    // Tout le UI ici — pas de ViewModel reference
}
```

**Règle MVVM** : La Screen appelle `viewModel.method()` directement (pas d'events/effects). La navigation se fait via les lambdas passées en paramètre (`onContinue`, `onBack`, etc.).

---

## Color.kt

```kotlin
package com.airline.checkin.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary (Midnight Sky) ──────────────────────────────────────────────────
val Primary            = Color(0xFF051849)
val OnPrimary          = Color(0xFFFFFFFF)
val PrimaryContainer   = Color(0xFF1E2E5F)
val OnPrimaryContainer = Color(0xFF8897CE)
val InversePrimary     = Color(0xFFB5C4FF)

// ── Secondary (Solar Gold) ──────────────────────────────────────────────────
val Secondary            = Color(0xFF795900)
val OnSecondary          = Color(0xFFFFFFFF)
val SecondaryContainer   = Color(0xFFFEBF0D)
val OnSecondaryContainer = Color(0xFF6D5000)

// ── Tertiary (Warm Bronze) ──────────────────────────────────────────────────
val Tertiary            = Color(0xFF2F1500)
val OnTertiary          = Color(0xFFFFFFFF)
val TertiaryContainer   = Color(0xFF4D2700)
val OnTertiaryContainer = Color(0xFFC68C5C)

// ── Error ───────────────────────────────────────────────────────────────────
val Error            = Color(0xFFBA1A1A)
val OnError          = Color(0xFFFFFFFF)
val ErrorContainer   = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

// ── Surface & Background ────────────────────────────────────────────────────
val Background          = Color(0xFFF8F9FF)
val OnBackground        = Color(0xFF0B1C30)
val Surface             = Color(0xFFF8F9FF)
val OnSurface           = Color(0xFF0B1C30)
val SurfaceDim          = Color(0xFFCBDBF5)
val SurfaceBright       = Color(0xFFF8F9FF)
val SurfaceContainerLowest  = Color(0xFFFFFFFF)
val SurfaceContainerLow     = Color(0xFFEFF4FF)
val SurfaceContainer        = Color(0xFFE5EEFF)
val SurfaceContainerHigh    = Color(0xFFDCE9FF)
val SurfaceContainerHighest = Color(0xFFD3E4FE)
val OnSurfaceVariant    = Color(0xFF45464F)
val InverseSurface      = Color(0xFF213145)
val InverseOnSurface    = Color(0xFFEAF1FF)

// ── Outline ─────────────────────────────────────────────────────────────────
val Outline        = Color(0xFF757680)
val OutlineVariant = Color(0xFFC5C6D0)
val SurfaceTint    = Color(0xFF4D5C90)

// ── Fixed variants ──────────────────────────────────────────────────────────
val PrimaryFixed           = Color(0xFFDCE1FF)
val PrimaryFixedDim        = Color(0xFFB5C4FF)
val OnPrimaryFixed         = Color(0xFF041749)
val OnPrimaryFixedVariant  = Color(0xFF354476)
val SecondaryFixed         = Color(0xFFFFDFA0)
val SecondaryFixedDim      = Color(0xFFFBBC05)
val OnSecondaryFixed       = Color(0xFF261A00)
val OnSecondaryFixedVariant= Color(0xFF5C4300)

// ── Semantic shortcuts (utilise ces aliases dans les composants) ─────────────
val Gold       = SecondaryContainer   // #FEBF0D — CTAs promus, sélection active
val MidnightBg = PrimaryContainer     // #1E2E5F — headers, boutons primaires
val CardSurface = SurfaceContainerLowest // #FFFFFF — fond des cards
```

---

## Typography.kt

> Les fonts **Plus Jakarta Sans** et **Inter** doivent être ajoutées dans `res/font/` ou via Google Fonts dependency.

```kotlin
package com.airline.checkin.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.airline.checkin.R

// ── Font Families ────────────────────────────────────────────────────────────
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular,  FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,     FontWeight.Bold)
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium,  FontWeight.Medium),
    Font(R.font.inter_semibold,FontWeight.SemiBold)
)

// ── Typography Scale ─────────────────────────────────────────────────────────
val AirlineTypography = Typography(

    // Display → Plus Jakarta Sans Bold 48sp
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize   = 48.sp,
        lineHeight = 52.8.sp   // 1.1 × 48
    ),

    // Headline → Plus Jakarta Sans SemiBold 24sp
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 31.2.sp   // 1.3 × 24
    ),

    // Title → Plus Jakarta Sans SemiBold 18sp
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 25.2.sp   // 1.4 × 18
    ),

    // Body base → Inter Regular 16sp
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 25.6.sp   // 1.6 × 16
    ),

    // Body small → Inter Regular 14sp
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 21.sp     // 1.5 × 14
    ),

    // Label caps → Inter SemiBold 12sp (codes IATA : JFK, LHR…)
    labelSmall = TextStyle(
        fontFamily    = Inter,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 12.sp,
        lineHeight    = 14.4.sp,
        letterSpacing = 0.05.sp
    )
)
```

**Usage dans les composables :**
```kotlin
// Code aéroport
Text("JFK", style = MaterialTheme.typography.labelSmall)

// Titre de section
Text("Seat Selection", style = MaterialTheme.typography.headlineMedium)

// Corps de texte
Text("Your flight departs at 08:45", style = MaterialTheme.typography.bodyLarge)
```

---

## Shape.kt

```kotlin
package com.airline.checkin.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AirlineShapes = Shapes(
    // Boutons, Inputs, petits éléments → 8dp
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),

    // Cards, Containers → 16dp
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),

    // Modals, bottom sheets → 24dp
    extraLarge = RoundedCornerShape(24.dp)
)

// Shapes utilitaires (hors Material scale)
val ShapePill   = RoundedCornerShape(percent = 50)  // Search bar, Flight chips
val ShapeCard   = RoundedCornerShape(16.dp)          // Toutes les cards
val ShapeButton = RoundedCornerShape(8.dp)           // Boutons standard
val ShapeInput  = RoundedCornerShape(8.dp)           // TextFields
val ShapeChip   = RoundedCornerShape(8.dp)           // "Non-stop", "Business"
```

---

## AirlineTheme.kt

```kotlin
package com.airline.checkin.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AirlightColorScheme = lightColorScheme(
    primary                = Primary,
    onPrimary              = OnPrimary,
    primaryContainer       = PrimaryContainer,
    onPrimaryContainer     = OnPrimaryContainer,
    inversePrimary         = InversePrimary,

    secondary              = Secondary,
    onSecondary            = OnSecondary,
    secondaryContainer     = SecondaryContainer,
    onSecondaryContainer   = OnSecondaryContainer,

    tertiary               = Tertiary,
    onTertiary             = OnTertiary,
    tertiaryContainer      = TertiaryContainer,
    onTertiaryContainer    = OnTertiaryContainer,

    error                  = Error,
    onError                = OnError,
    errorContainer         = ErrorContainer,
    onErrorContainer       = OnErrorContainer,

    background             = Background,
    onBackground           = OnBackground,
    surface                = Surface,
    onSurface              = OnSurface,
    onSurfaceVariant       = OnSurfaceVariant,
    surfaceVariant         = SurfaceVariant,      // = SurfaceContainerHighest
    inverseSurface         = InverseSurface,
    inverseOnSurface       = InverseOnSurface,

    outline                = Outline,
    outlineVariant         = OutlineVariant,
    surfaceTint            = SurfaceTint
)

@Composable
fun AirlineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AirlightColorScheme,
        typography  = AirlineTypography,
        shapes      = AirlineShapes,
        content     = content
    )
}
```

---

## Elevation & Shadows

Material 3 gère l'élévation via `tonalElevation` et `shadowElevation`. Correspondance avec le design system :

```kotlin
// Level 0 — Background (pas de card)
// Rien à faire, c'est le Background color

// Level 1 — Cards standard
Card(
    elevation = CardDefaults.cardElevation(
        defaultElevation = 2.dp,   // shadow légère
        hoveredElevation = 4.dp    // lift au hover/focus
    ),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface  // #FFFFFF
    ),
    shape = ShapeCard
) { ... }

// Level 2 — Cards interactives (sélection de siège, vol sélectionné)
Card(
    elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp,
        pressedElevation = 8.dp
    )
) { ... }
```

---

## Spacing Constants

```kotlin
package com.airline.checkin.presentation.ui.theme

import androidx.compose.ui.unit.dp

object Spacing {
    val xs        = 4.dp
    val sm        = 8.dp    // base unit
    val smPlus    = 12.dp
    val md        = 16.dp
    val mdPlus    = 24.dp
    val lg        = 32.dp
    val xl        = 48.dp
    val xxl       = 80.dp
    val gutter    = 24.dp   // padding horizontal des screens
    val cardPad   = 24.dp   // padding interne des cards
    val sectionGap= 80.dp   // séparation entre grandes sections
}
```

Usage :
```kotlin
.padding(horizontal = Spacing.gutter)
.padding(vertical   = Spacing.mdPlus)
Spacer(modifier = Modifier.height(Spacing.sectionGap))
```

---

## Composants visuels — règles de style

### Bouton Primaire
```kotlin
Button(
    onClick = { ... },
    colors  = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer, // #1E2E5F
        contentColor   = MaterialTheme.colorScheme.onPrimary         // #FFFFFF
    ),
    shape = ShapeButton
) {
    Text("Continue", style = MaterialTheme.typography.bodyLarge)
}
```

### Bouton CTA Gold (Upgrade / Promoted)
```kotlin
Button(
    onClick = { ... },
    colors  = ButtonDefaults.buttonColors(
        containerColor = Gold,                                        // #FEBF0D
        contentColor   = MaterialTheme.colorScheme.onSecondaryContainer // #6D5000
    ),
    shape = ShapeButton
) { ... }
```

### Bouton Secondaire (Ghost)
```kotlin
OutlinedButton(
    onClick      = { ... },
    border       = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
    shape        = ShapeButton
) {
    Text("Download PDF", color = MaterialTheme.colorScheme.primaryContainer)
}
```

### TextField avec floating label
```kotlin
OutlinedTextField(
    value         = value,
    onValueChange = onValueChange,
    label         = { Text(label) },
    shape         = ShapeInput,
    colors        = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor    = MaterialTheme.colorScheme.primary
    )
)
```

### Flight Chip (Non-stop / Business / Eco)
```kotlin
SuggestionChip(
    onClick = {},
    label   = { Text("Non-stop", style = MaterialTheme.typography.labelSmall) },
    shape   = ShapeChip,
    colors  = SuggestionChipDefaults.suggestionChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
)
```

---

## Checklist thème pour l'agent

Avant de générer un composant, vérifier :

- [ ] Couleurs → `MaterialTheme.colorScheme.X` uniquement (pas de `Color(0xFF...)` hardcodé)
- [ ] Typo → `MaterialTheme.typography.X` (voir mapping dans Typography.kt)
- [ ] Shapes → `ShapeCard`, `ShapeButton`, `ShapeInput`, `ShapePill`, `ShapeChip`
- [ ] Spacing → `Spacing.X` (pas de valeurs `.dp` arbitraires)
- [ ] Cards → `CardDefaults.cardElevation(defaultElevation = 2.dp)` + `ShapeCard`
- [ ] CTA principal → `primaryContainer` (#1E2E5F) fond, `onPrimary` texte
- [ ] CTA gold → `Gold` (#FEBF0D) fond, `onSecondaryContainer` texte
- [ ] Codes aéroport (JFK, LHR) → `typography.labelSmall` obligatoire