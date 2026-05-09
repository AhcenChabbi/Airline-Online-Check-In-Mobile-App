# COMPOSE UI SKILL — Airline Check-In App · MVVM
> **Agent directive**: Lis ce fichier entièrement avant de générer n'importe quel écran. Tu es un senior Android/Compose engineer. Pattern : **MVVM pur** avec Jetpack Compose + Hilt + Material 3. Ne jamais utiliser MVI (pas d'UiEvent, pas d'UiEffect, pas d'onEvent()).

---

## 1. Architecture Contract

Ce projet suit **Clean Architecture + MVVM**. Respecte strictement ces frontières :

| Layer | Responsabilité |
|---|---|
| `presentation/ui/screens/` | Screens — orchestrateurs minces. Collectent le state, passent des lambdas. Zéro logique métier. |
| `presentation/ui/viewmodels/` | ViewModels — exposent `StateFlow<UiState>`, contiennent la logique de présentation, appellent les UseCases |
| `presentation/ui/components/` | Composables réutilisables — stateless, génériques, paramétrés |
| `presentation/ui/state/` | `XxxUiState` data classes uniquement — pas d'events ni d'effects |
| `presentation/navigation/` | `NavGraph`, `Screen`, `AppNavHost` — la navigation est gérée ici via les lambdas |

---

## 2. Pattern MVVM — Règles obligatoires

### ViewModel
```kotlin
@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val startCheckIn    : StartCheckInUseCase,
    private val scanPassport    : ScanPassportUseCase,
    private val selectSeat      : SelectSeatUseCase,
    private val declareBaggage  : DeclareBaggageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    // ✅ Méthodes nommées et explicites
    fun startCheckIn(bookingRef: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            startCheckIn(bookingRef)
                .onSuccess { checkin ->
                    _uiState.update { it.copy(checkIn = checkin, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun selectSeat(seatId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            selectSeat(seatId)
                .onSuccess { seat ->
                    _uiState.update { it.copy(selectedSeat = seat, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
```

### UiState
```kotlin
// presentation/ui/state/CheckInUiState.kt
data class CheckInUiState(
    val flight       : Flight?   = null,
    val checkIn      : CheckIn?  = null,
    val selectedSeat : Seat?     = null,
    val currentStep  : Int       = 0,
    val isLoading    : Boolean   = false,
    val error        : String?   = null,
    val isOffline    : Boolean   = false
)
```

### Screen — Pattern obligatoire
```kotlin
@Composable
fun SeatSelectionScreen(
    // ✅ Navigation via lambdas — jamais navController dans le composable
    onSeatConfirmed : () -> Unit,
    onBack          : () -> Unit,
    viewModel       : CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✅ Charger les données au démarrage si nécessaire
    LaunchedEffect(Unit) {
        viewModel.loadSeatMap()
    }

    SeatSelectionContent(
        uiState         = uiState,
        onSeatSelected  = viewModel::selectSeat,       // ✅ méthode directe
        onConfirm       = {
            if (uiState.selectedSeat != null) onSeatConfirmed()
        },
        onBack          = onBack,
        onRetry         = viewModel::loadSeatMap,
        onDismissError  = viewModel::clearError
    )
}

@Composable
private fun SeatSelectionContent(
    uiState        : CheckInUiState,
    onSeatSelected : (String) -> Unit,
    onConfirm      : () -> Unit,
    onBack         : () -> Unit,
    onRetry        : () -> Unit,
    onDismissError : () -> Unit
) {
    // Tout le UI ici — aucune référence au ViewModel
}
```

**Règles absolues :**
- ❌ Jamais `viewModel.onEvent(XxxEvent.Something)` — c'est du MVI
- ❌ Jamais `LaunchedEffect` pour collecter des effects — c'est du MVI
- ❌ Jamais `navController` à l'intérieur d'un composable
- ✅ Toujours passer `onXxx: () -> Unit` lambdas pour la navigation
- ✅ Toujours appeler `viewModel.method()` directement depuis la Screen function (pas depuis Content)

---

## 3. Structure des fichiers state/

```
presentation/ui/state/
  ├── CheckInUiState.kt        # data class — état du flow check-in
  ├── FlightUiState.kt         # data class — état de la recherche vol
  ├── SeatUiState.kt           # data class — état de la sélection siège
  ├── BaggageUiState.kt        # data class — état déclaration bagages
  ├── AuthUiState.kt           # data class — état login/register
  └── BoardingPassUiState.kt   # data class — état carte d'embarquement
```

**Pas de fichiers Event ni Effect** — ces concepts n'existent pas en MVVM pur.

---

## 4. Navigation — NavGraph Pattern

```kotlin
// navigation/NavGraph.kt
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.FlightLookup.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.SeatSelection.route) {
            SeatSelectionScreen(
                onSeatConfirmed = { navController.navigate(Screen.Baggage.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BoardingPass.route) {
            BoardingPassScreen(
                onBack = { navController.navigate(Screen.FlightLookup.route) {
                    popUpTo(Screen.FlightLookup.route) { inclusive = false }
                }}
            )
        }
    }
}
```

---

## 5. Catalog des Composants — Ne jamais recréer

Ces composants existent dans `presentation/ui/components/`. Toujours importer et réutiliser.

### 5.1 `StepProgressBar`
```kotlin
StepProgressBar(
    steps       = listOf("Passport", "Seat", "Baggage", "Review"),
    currentStep = uiState.currentStep  // 0-indexed
)
```
Obligatoire sur tous les écrans du flow check-in.

---

### 5.2 `FlightInfoCard`
```kotlin
FlightInfoCard(
    flight      = uiState.flight,
    isCollapsed = true   // false = version complète, true = header compact
)
```
Obligatoire en haut de chaque écran du flow pour ancrer le contexte.

---

### 5.3 `SeatMapView`
```kotlin
SeatMapView(
    seatMap        = uiState.seatMap,
    selectedSeatId = uiState.selectedSeat?.id,
    onSeatSelected = onSeatSelected   // (String) -> Unit
)
```
Ne jamais reconstruire une grille de sièges inline.

---

### 5.4 `PassportScannerView`
```kotlin
PassportScannerView(
    isScanning   = uiState.isScanning,
    onScanResult = viewModel::onPassportScanned,
    onError      = viewModel::onScanError
)
```

---

### 5.5 `BoardingPassCard`
```kotlin
BoardingPassCard(
    boardingPass = uiState.boardingPass,
    onDownload   = viewModel::downloadPdf,
    onShare      = viewModel::shareBoardingPass
)
```

---

### 5.6 `QRCodeView`
```kotlin
QRCodeView(
    data        = uiState.boardingPass.qrPayload,
    size        = 200.dp,
    contentDesc = "Boarding QR code"
)
```

---

### 5.7 `LoadingOverlay`
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    // ... contenu principal

    if (uiState.isLoading) LoadingOverlay()  // toujours en dernier dans le Box
}
```

---

### 5.8 `ErrorBanner`
```kotlin
if (uiState.error != null) {
    ErrorBanner(
        message  = uiState.error,
        onRetry  = onRetry,
        onDismiss = onDismissError
    )
}
```

---

## 6. Composants Primitifs

Toujours utiliser ces primitives — jamais les composants Material3 raw directement :

| Composant | Signature | Usage |
|---|---|---|
| `PrimaryButton` | `(text, enabled, onClick)` | CTA principal de chaque écran |
| `GoldButton` | `(text, onClick)` | Upgrade, actions promues |
| `SecondaryButton` | `(text, onClick)` | Télécharger, Partager |
| `AirlineTopBar` | `(title, onBack?, actions?)` | Top bar de chaque écran |
| `LabeledTextField` | `(label, value, onValueChange, error?)` | Tous les champs de formulaire |
| `SectionHeader` | `(title)` | Séparateur de groupes |
| `InfoRow` | `(label, value)` | Affichage clé/valeur (DetailsReview) |
| `SuccessBanner` | `(title, subtitle)` | États de confirmation |
| `ErrorBanner` | `(message, onRetry?, onDismiss?)` | Tous les états d'erreur |
| `OfflineBanner` | `()` | Mode hors-ligne |
| `FlightChip` | `(label, type)` | "Non-stop", "Business", "Eco" |

Si un primitif manque → le créer dans `components/` d'abord, puis l'utiliser.

---

## 7. Template Standard d'un Écran

```kotlin
@Composable
private fun ScreenContent(
    uiState        : XxxUiState,
    onAction       : (String) -> Unit,   // méthodes viewmodel passées en lambda
    onConfirm      : () -> Unit,
    onBack         : () -> Unit,
    onRetry        : () -> Unit,
    onDismissError : () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                AirlineTopBar(title = "Titre", onBack = onBack)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.gutter)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.mdPlus)
            ) {
                // 1. Contexte du vol (obligatoire sur tous les écrans flow)
                FlightInfoCard(flight = uiState.flight, isCollapsed = true)

                // 2. Progression (obligatoire sur tous les écrans flow)
                StepProgressBar(steps = CHECK_IN_STEPS, currentStep = uiState.currentStep)

                // 3. Erreur (si présente)
                if (uiState.error != null) {
                    ErrorBanner(
                        message   = uiState.error,
                        onRetry   = onRetry,
                        onDismiss = onDismissError
                    )
                }

                // 4. Contenu spécifique à l'écran
                // ...

                Spacer(modifier = Modifier.weight(1f))

                // 5. CTA principal — toujours en bas
                PrimaryButton(
                    text    = "Continuer",
                    enabled = uiState.canProceed,
                    onClick = onConfirm
                )
            }
        }

        // 6. Loading overlay — toujours en dernier dans le Box
        if (uiState.isLoading) LoadingOverlay()
    }
}
```

---

## 8. Gestion Offline & Async

```kotlin
// UiState modélise toujours tous les cas
data class FlightUiState(
    val flight    : Flight?  = null,
    val isLoading : Boolean  = false,
    val error     : String?  = null,
    val isOffline : Boolean  = false
)

// Le contenu réagit à tous les cas
Column {
    when {
        uiState.isOffline          -> OfflineBanner()
        uiState.error    != null   -> ErrorBanner(uiState.error, onRetry = onRetry)
        uiState.flight   != null   -> FlightInfoCard(uiState.flight)
        else                       -> { /* état vide si nécessaire */ }
    }
}
// LoadingOverlay séparé dans le Box parent — toujours visible même sur erreur
```

---

## 9. Mapping Écran → Composants

| Écran | Composants requis |
|---|---|
| `LoginScreen` | `LabeledTextField` × 2, `PrimaryButton`, `ErrorBanner` |
| `RegisterScreen` | `LabeledTextField` × 4, `PrimaryButton`, `ErrorBanner` |
| `FlightLookupScreen` | `LabeledTextField`, `PrimaryButton`, `ErrorBanner` |
| `FlightDetailScreen` | `FlightInfoCard`, `SectionHeader`, `InfoRow`, `PrimaryButton` |
| `CheckInFlowScreen` | `StepProgressBar`, `FlightInfoCard`, `LoadingOverlay` |
| `PassportScanScreen` | `PassportScannerView`, `StepProgressBar`, `FlightInfoCard`, `ErrorBanner` |
| `DetailsReviewScreen` | `FlightInfoCard`, `InfoRow`, `SectionHeader`, `PrimaryButton` |
| `SeatSelectionScreen` | `SeatMapView`, `StepProgressBar`, `FlightInfoCard`, `PrimaryButton` |
| `BaggageDeclarationScreen` | `StepProgressBar`, `FlightInfoCard`, `LabeledTextField`, `PrimaryButton` |
| `SpecialRequestsScreen` | `StepProgressBar`, `FlightInfoCard`, `PrimaryButton` |
| `BoardingPassScreen` | `BoardingPassCard`, `QRCodeView`, `SuccessBanner`, `SecondaryButton` |
| `OfflineBoardingScreen` | `BoardingPassCard`, `QRCodeView`, `OfflineBanner` |
| `ConfirmationScreen` | `SuccessBanner`, `BoardingPassCard`, `QRCodeView`, `PrimaryButton` |

---

## 10. Checklist avant de soumettre un écran

- [ ] ViewModel n'a que des `fun method()` — pas de `onEvent()`, pas de `Channel`, pas d'`Effect`
- [ ] Screen collecte le state avec `collectAsStateWithLifecycle()`
- [ ] Navigation via lambdas `onXxx: () -> Unit` passées en paramètre
- [ ] Contenu dans une fonction `private fun XxxContent(...)` séparée — sans référence au ViewModel
- [ ] `FlightInfoCard` + `StepProgressBar` présents sur tous les écrans du flow
- [ ] `LoadingOverlay` en dernier dans le `Box` racine
- [ ] `ErrorBanner` gère le state `uiState.error`
- [ ] Zéro couleur, taille ou string hardcodée — `MaterialTheme`, `Spacing.X`, `stringResource()`
- [ ] Tous les nouveaux composables ont un `@Preview`
- [ ] Nouveaux composants extraits dans `components/` avec leur propre fichier