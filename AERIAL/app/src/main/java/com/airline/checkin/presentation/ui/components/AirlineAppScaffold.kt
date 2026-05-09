package com.airline.checkin.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun AirlineAppScaffold(
        companyName: String,
        selectedRoute: String,
        onBottomItemClick: (BottomNavItem) -> Unit,
        onNotificationClick: () -> Unit,
        content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
            topBar = {
                AirlineTopBar(companyName = companyName, onNotificationClick = onNotificationClick)
            },
            bottomBar = {
                AirlineBottomNavBar(
                        items = DefaultBottomNavItems,
                        selectedRoute = selectedRoute,
                        onItemClick = onBottomItemClick
                )
            },
            content = content
    )
}
