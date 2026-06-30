# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.airline.checkin.SomeTestClass"

# Clean build
./gradlew clean assembleDebug
```

The project uses **KSP** (not KAPT) for annotation processing (Hilt, Room). If build errors appear after adding annotations, run `./gradlew clean` first.

## Architecture: Clean Architecture + MVVM

Three layers with strict dependency flow: `presentation` → `domain` → `data`.

```
core/           — DI modules, network interceptors, security, services, utils
domain/         — Pure Kotlin: models, repository interfaces, use cases
data/           — Implementations: Room (local), Retrofit (remote), mappers
presentation/   — Jetpack Compose UI: screens, viewmodels, components, state, navigation
```

**Pattern is MVVM — never MVI.** No `UiEvent`, no `UiEffect`, no `onEvent()`, no `Channel`. ViewModels expose named `fun method()` functions and a single `StateFlow<XxxUiState>`.

## DI (Hilt)

Four Hilt modules in `core/di/`:
- `NetworkModule` — OkHttp + Retrofit + 4 API interfaces (Auth, Flight, CheckIn, BoardingPass)
- `RepositoryModule` — binds domain repository interfaces to their `data/repository/` impls
- `RoomModule` — `AppDatabase` + 6 DAOs
- `UseCaseModule` / `SecurityModule` — remaining bindings

## Network & Auth

`AuthInterceptor` auto-attaches the Bearer token to every request and handles silent token refresh on 401. `TokenManager` stores tokens in `EncryptedSharedPreferences` (initialized in `AirlineApplication.onCreate()`). The backend base URL must be added to `network_security_config.xml` if it uses HTTP (cleartext already allowed for `10.0.2.2`, `localhost`, `127.0.0.1`, `192.168.35.120`).

## Check-In Session State

The multi-step check-in flow accumulates state via `CheckInSessionStore` (a singleton `@Inject`ed into `CheckInViewModel`). It holds the in-flight `CheckInSession` across steps: booking → passport scan → seat → baggage → special requests → boarding pass. Call `CheckInSessionStore.reset()` when starting a new flow.

## ViewModel Pattern

```kotlin
@HiltViewModel
class XxxViewModel @Inject constructor(
    private val someUseCase: SomeUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(XxxUiState())
    val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()

    fun doThing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            someUseCase()
                .onSuccess { result -> _uiState.update { it.copy(data = result, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}
```

`UiState` is a plain `data class` in `presentation/ui/state/` with `isLoading`, `error`, and domain fields. No events or effects.

## Screen Pattern

```kotlin
@Composable
fun SomeScreen(
    onNext: () -> Unit,          // navigation via lambdas only
    onBack: () -> Unit,
    viewModel: XxxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.loadData() }
    SomeContent(uiState, onAction = viewModel::doThing, onNext = onNext, onBack = onBack,
                onRetry = viewModel::loadData, onDismissError = viewModel::clearError)
}

@Composable
private fun SomeContent(uiState: XxxUiState, ...) {
    // All UI here — zero ViewModel references
}
```

**Never** pass `NavController` into a composable. **Never** call `navController.navigate()` inside a screen composable.

## Standard Screen Layout (check-in flow screens)

```kotlin
Box(Modifier.fillMaxSize()) {
    Scaffold(topBar = { AirlineTopBar(title, onBack) }) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = Spacing.gutter).verticalScroll(...)) {
            FlightInfoCard(flight = uiState.flight, isCollapsed = true)  // required
            StepProgressBar(steps = CHECK_IN_STEPS, currentStep = uiState.currentStep)  // required
            if (uiState.error != null) ErrorBanner(uiState.error, onRetry, onDismissError)
            // screen content
            Spacer(Modifier.weight(1f))
            PrimaryButton(text = "Continue", enabled = uiState.canProceed, onClick = onNext)
        }
    }
    if (uiState.isLoading) LoadingOverlay()  // always last in Box
}
```

## Reusable Components (`presentation/ui/components/`)

Do not recreate these — import from the existing components package:

| Component | Purpose |
|---|---|
| `StepProgressBar` | Check-in flow step indicator |
| `FlightInfoCard` | Flight context header (collapsed or full) |
| `SeatMapView` | Seat grid — never rebuild inline |
| `BoardingPassCard` | Boarding pass with download/share |
| `QRCodeView` | QR code display via ZXing |
| `PassportScannerView` / `PassportCameraScanner` | MRZ passport OCR via CameraX + ML Kit |
| `LoadingOverlay` | Full-screen loading (always last in `Box`) |
| `ErrorBanner` / `SuccessBanner` / `OfflineBanner` | Status banners |
| `PrimaryButton` / `SecondaryButton` / `GoldButton` | Button hierarchy |
| `LabeledTextField` / `AeroAuthTextField` | Form inputs |
| `AirlineTopBar` / `AirlineBottomNavBar` | Navigation chrome |

## Navigation

Routes are defined in `Screen.kt` (sealed class). All composable destinations wired in `AppNavHost.kt`. Bottom navigation covers: Home, Check-In flow, Boarding pass, Profile.

## Design System

- **Colors**: Deep midnight blue (`#051849` primary), solar gold (`#febf0d` accent). Use `MaterialTheme.colorScheme.*` — never hardcode hex.
- **Typography**: Plus Jakarta Sans (headlines), Inter (body). Defined in `theme/Typography.kt`.
- **Spacing**: 8px base unit via `Spacing` object (`Spacing.gutter = 24dp`, `Spacing.md = 24dp`, etc.).
- **Shapes**: 8px buttons/inputs, 16px cards. Use `MaterialTheme.shapes.*`.

Never hardcode colors, sizes, or strings. Use `MaterialTheme`, `Spacing.X`, and `stringResource()`.

## Key Technologies

| Tech | Version | Role |
|---|---|---|
| Kotlin | 2.0.21 | Language |
| Jetpack Compose BOM | 2024.09.00 | UI framework |
| Hilt | 2.59.2 | Dependency injection |
| Room | 2.7.0 | Local database |
| Retrofit | 2.11.0 | REST client |
| Navigation Compose | 2.7.7 | In-app navigation |
| CameraX | 1.3.1 | Camera for passport scan |
| ML Kit Text Recognition | 16.0.1 | MRZ extraction |
| Firebase Messaging | via BoM 33.1.0 | Push notifications |
| ZXing Android Embedded | 4.3.0 | QR code generation |
| Coil | 2.6.0 | Image loading |

## Offline Support

`NetworkMonitor` tracks connectivity (initialized in `AirlineApplication`). Boarding passes are persisted to Room via `BoardingPassDao` so `OfflineBoardingScreen` can display them without network access.
