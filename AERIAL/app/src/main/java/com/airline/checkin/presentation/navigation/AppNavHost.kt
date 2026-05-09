package com.airline.checkin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.airline.checkin.presentation.ui.screens.auth.LoginScreen
import com.airline.checkin.presentation.ui.screens.auth.RegisterScreen
import com.airline.checkin.presentation.ui.screens.auth.SplashScreen
import com.airline.checkin.presentation.ui.screens.flight.FlightDetailScreen
import com.airline.checkin.presentation.ui.screens.flight.FlightLookupScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.HOME) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.HOME) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.HOME) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
                com.airline.checkin.presentation.ui.components.AirlineTopBar(companyName = androidx.compose.ui.res.stringResource(com.airline.checkin.R.string.company_name), onNotificationClick = {})
                androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(com.airline.checkin.R.string.home_dashboard_wip))
                }
            }
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.HOME) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FlightLookup.route) {
            FlightLookupScreen(
                    onFlightSelected = { navController.navigate(Screen.FlightDetail.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.FlightLookup.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.FlightDetail.route) {
            FlightDetailScreen(
                    onContinueToCheckIn = { navController.navigate(Screen.PassportScan.route) },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PassportScan.route) {
            com.airline.checkin.presentation.ui.screens.checkin.PassportScanScreen(
                    onScanComplete = { navController.navigate(Screen.DetailsReview.route) },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DetailsReview.route) {
            com.airline.checkin.presentation.ui.screens.checkin.DetailsReviewScreen(
                    onContinue = { navController.navigate(Screen.SeatSelection.route) },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeatSelection.route) {
            com.airline.checkin.presentation.ui.screens.seat.SeatSelectionScreen(
                    onSeatConfirmed = { navController.navigate(Screen.BaggageDeclaration.route) },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BaggageDeclaration.route) {
            com.airline.checkin.presentation.ui.screens.baggage.BaggageDeclarationScreen(
                    onContinue = { navController.navigate(Screen.SpecialRequests.route) },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SpecialRequests.route) {
            com.airline.checkin.presentation.ui.screens.special.SpecialRequestsScreen(
                    onContinue = { navController.navigate(Screen.Confirmation.route) },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Confirmation.route) {
            com.airline.checkin.presentation.ui.screens.checkin.ConfirmationScreen(
                    onContinue = {
                        navController.navigate(Screen.BoardingPass.route) {
                            popUpTo(Screen.FlightLookup.route) { inclusive = false }
                        }
                    },
                    onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BoardingPass.route) {
            com.airline.checkin.presentation.ui.screens.boarding.BoardingPassScreen(
                    onBack = {
                        navController.navigate(Screen.FlightLookup.route) {
                            popUpTo(Screen.BoardingPass.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.OfflineBoarding.route) {
            com.airline.checkin.presentation.ui.screens.boarding.OfflineBoardingScreen(
                    onBack = {
                        navController.navigate(Screen.FlightLookup.route) {
                            popUpTo(Screen.OfflineBoarding.route) { inclusive = true }
                        }
                    }
            )
        }
    }
}
