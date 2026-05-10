package com.airline.checkin.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplaneTicket
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airline.checkin.R
import com.airline.checkin.presentation.navigation.Screen
import com.airline.checkin.presentation.ui.theme.AirlineTheme

data class BottomNavItem(val route: String, val labelRes: Int, val icon: ImageVector)

val DefaultBottomNavItems = listOf(
    BottomNavItem(route = Screen.HOME,                labelRes = R.string.nav_home,     icon = Icons.Rounded.Home),
    BottomNavItem(route = Screen.FlightLookup.route,  labelRes = R.string.nav_checkin,  icon = Icons.Rounded.Search),
    BottomNavItem(route = Screen.BoardingPass.route,  labelRes = R.string.nav_boarding, icon = Icons.Rounded.AirplaneTicket),
    BottomNavItem(route = Screen.PROFILE,             labelRes = R.string.nav_profile,  icon = Icons.Rounded.Person)
)

/** Screens that should NOT display the bottom bar */
private val noBottomBarRoutes = setOf(
    Screen.Splash.route,
    Screen.Login.route,
    Screen.Register.route
)

/**
 * NavController-aware bottom navigation bar.
 * Hides itself on auth / splash screens automatically.
 */
@Composable
fun AirlineBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    if (currentRoute in noBottomBarRoutes) return

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp
    ) {
        DefaultBottomNavItems.forEach { item ->
            val label = stringResource(id = item.labelRes)
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = label) },
                label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor   = MaterialTheme.colorScheme.primaryContainer,
                    indicatorColor      = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/** Static preview overload (for tooling only) */
@Composable
fun AirlineBottomNavBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val label = stringResource(id = item.labelRes)
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemClick(item) },
                icon = { Icon(imageVector = item.icon, contentDescription = label) },
                label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor   = MaterialTheme.colorScheme.primaryContainer,
                    indicatorColor      = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AirlineBottomNavBarPreview() {
    AirlineTheme {
        AirlineBottomNavBar(items = DefaultBottomNavItems, selectedRoute = "home", onItemClick = {})
    }
}
