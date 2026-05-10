package com.airline.checkin.presentation.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airline.checkin.R
import com.airline.checkin.presentation.ui.components.AirlineTopBar
import com.airline.checkin.presentation.ui.components.SectionHeader
import com.airline.checkin.presentation.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    userName: String = "User",
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = stringResource(R.string.company_name),
                onNotificationClick = onNavigateToNotifications
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1. Auto-rotating Slider
            HomeSlider()

            Spacer(modifier = Modifier.height(Spacing.md))

            // 2. Quick Actions
            QuickActionsGrid(onNavigateToSearch)

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Our Services
            CompanyServicesSection()

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun HomeSlider() {
    val banners = listOf(
        BannerData("Exclusive Deals", "Fly to London from $299", "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad"),
        BannerData("New Route", "Explore the beauty of Tokyo", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf"),
        BannerData("Safe Travels", "Your safety is our priority", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34")
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Auto-rotation logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = Spacing.gutter, vertical = Spacing.md)
            .clip(RoundedCornerShape(16.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val banner = banners[page]
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Spacing.md)
                ) {
                    Text(
                        text = banner.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = banner.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Page Indicator
        Row(
            Modifier
                .height(20.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(onNavigateToSearch: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = Spacing.gutter)) {
        SectionHeader(title = "Quick Actions")
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            QuickActionItem(
                title = "Check-in",
                icon = Icons.Rounded.AppRegistration,
                color = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToSearch,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                title = "My Trips",
                icon = Icons.Rounded.Luggage,
                color = Color(0xFF4CAF50),
                onClick = { },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            QuickActionItem(
                title = "Status",
                icon = Icons.Rounded.FlightTakeoff,
                color = Color(0xFFFF9800),
                onClick = { },
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                title = "Offers",
                icon = Icons.Rounded.LocalOffer,
                color = Color(0xFFE91E63),
                onClick = { },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CompanyServicesSection() {
    Column(modifier = Modifier.padding(horizontal = Spacing.gutter)) {
        SectionHeader(title = "Company Services")
        
        ServiceCard(
            title = "Priority Boarding",
            description = "Skip the line and board first with our priority service.",
            icon = Icons.Rounded.Stars
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        ServiceCard(
            title = "Extra Baggage",
            description = "Need more space? Add extra baggage to your booking easily.",
            icon = Icons.Rounded.AddBusiness
        )
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        ServiceCard(
            title = "In-flight Meals",
            description = "Pre-order your favorite meals from our premium selection.",
            icon = Icons.Rounded.Restaurant
        )
    }
}

@Composable
private fun ServiceCard(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

data class BannerData(val title: String, val subtitle: String, val imageUrl: String)
