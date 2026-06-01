package com.airline.checkin.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object FlightLookup : Screen("flight_lookup")
    object FlightDetail : Screen("flight_detail")
    object PassportScan : Screen("passport_scan")
    object DetailsReview : Screen("details_review")
    object SeatSelection : Screen("seat_selection")
    object BaggageDeclaration : Screen("baggage_declaration")
    object SpecialRequests : Screen("special_requests")
    object Confirmation : Screen("confirmation")
    object BoardingPass : Screen("boarding_pass/{checkinId}") {
        fun createRoute(checkinId: String) = "boarding_pass/$checkinId"
    }
    object OfflineBoarding : Screen("offline_boarding/{checkinId}") {
        fun createRoute(checkinId: String) = "offline_boarding/$checkinId"
    }
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")

    companion object {
        const val HOME = "home"
        const val CHECKIN = "checkin"
        const val BOARDING = "boarding"
        const val PROFILE = "profile"
    }
}
