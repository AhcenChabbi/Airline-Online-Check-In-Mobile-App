package com.airline.checkin.presentation.ui.screens.baggage

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.theme.Gold

/**
 * Maps to backend Baggage model:
 * BagType: CARRY_ON, CHECKED, OVERSIZED, FRAGILE, SPORTS_EQUIPMENT
 */
@Composable
fun BaggageDeclarationScreen(onContinue: () -> Unit, onBack: () -> Unit) {
    var carryOnCount by remember { mutableIntStateOf(1) }
    var checkedCount by remember { mutableIntStateOf(0) }
    var oversizedCount by remember { mutableIntStateOf(0) }
    var fragileCount by remember { mutableIntStateOf(0) }
    var sportsCount by remember { mutableIntStateOf(0) }
    
    val checkedBagPrice = 45.0
    val specialBagPrice = 65.0
    
    val totalExtra = (if (checkedCount > 1) (checkedCount - 1) * checkedBagPrice else 0.0) +
                     (oversizedCount * specialBagPrice) +
                     (fragileCount * 20.0) +
                     (sportsCount * specialBagPrice)

    Scaffold(
        topBar = {
            AirlineTopBar(
                companyName = "AERIAL",
                onNotificationClick = {},
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        bottomBar = {
            BaggagePriceBar(
    import com.airline.checkin.presentation.ui.viewmodels.CheckInViewModel
    import androidx.hilt.navigation.compose.hiltViewModel
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import com.airline.checkin.domain.model.Baggage
                totalPrice = totalExtra,
                onNext = onContinue
            )
        }
    ) { innerPadding ->
    fun BaggageDeclarationScreen(
        onContinue: () -> Unit,
        onBack: () -> Unit,
        viewModel: CheckInViewModel = hiltViewModel()
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val checkInId = uiState.checkIn?.id
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.gutter)
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            // 1. Progress Bar
            StepProgressBar(currentStep = 3, totalSteps = 5)

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Baggage Declaration",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 2. Baggage Items (Mapped to BagType enum)
            
            // CARRY_ON
            BaggageItemCard(
                BaggagePriceBar(
                    totalPrice = totalExtra,
                    onNext = {
                        if (checkInId != null) {
                            val bags = buildList {
                                if (carryOnCount > 0) add(Baggage(bagType = "CARRY_ON", quantity = carryOnCount))
                                if (checkedCount > 0) add(Baggage(bagType = "CHECKED", quantity = checkedCount))
                                if (oversizedCount > 0) add(Baggage(bagType = "OVERSIZED", quantity = oversizedCount))
                                if (fragileCount > 0) add(Baggage(bagType = "FRAGILE", quantity = fragileCount))
                                if (sportsCount > 0) add(Baggage(bagType = "SPORTS_EQUIPMENT", quantity = sportsCount))
                            }
                            viewModel.declareBaggage(checkInId, bags)
                        }
                        onContinue()
                    }
                )
                badgeTextColor = MaterialTheme.colorScheme.primary,
                count = carryOnCount,
                onIncrement = { if (carryOnCount < 1) carryOnCount++ },
                onDecrement = { if (carryOnCount > 0) carryOnCount-- }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // CHECKED
            val isFirstCheckedIncluded = checkedCount <= 1 && checkedCount > 0
            BaggageItemCard(
                title = "Checked baggage",
                subtitle = "Max 23kg (Standard)",
                badgeText = if (checkedCount == 0) "None" else if (isFirstCheckedIncluded) "Included" else "+ $${checkedBagPrice.toInt()}/extra",
                badgeColor = if (isFirstCheckedIncluded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Gold,
                badgeTextColor = if (isFirstCheckedIncluded) MaterialTheme.colorScheme.primary else Color.White,
                count = checkedCount,
                onIncrement = { if (checkedCount < 3) checkedCount++ },
                onDecrement = { if (checkedCount > 0) checkedCount-- },
                isSpecial = checkedCount > 0
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // OVERSIZED / SPECIAL
            BaggageItemCard(
                title = "Oversized / Sports",
                subtitle = "Surfboards, Bikes, etc.",
                badgeText = "+ $${specialBagPrice.toInt()}/item",
                badgeColor = Gold.copy(alpha = 0.8f),
                badgeTextColor = Color.White,
                count = sportsCount + oversizedCount,
                onIncrement = { sportsCount++ },
                onDecrement = { if (sportsCount > 0) sportsCount-- else if (oversizedCount > 0) oversizedCount-- }
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // 3. Info Notice
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Spacing.md),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "Purchasing extra baggage online is up to 40% cheaper than paying at the airport counter. Weight limits apply per passenger.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
