package com.airline.checkin.presentation.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.airline.checkin.R
import com.airline.checkin.domain.model.User
import com.airline.checkin.presentation.navigation.Screen
import com.airline.checkin.presentation.ui.components.*
import com.airline.checkin.presentation.ui.state.ProfileUiState
import com.airline.checkin.presentation.ui.theme.Spacing
import com.airline.checkin.presentation.ui.viewmodels.AuthViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    navController: androidx.navigation.NavController
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val logoutState by viewModel.logoutState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getMe()
    }

    LaunchedEffect(logoutState) {
        if (logoutState) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.resetLogoutState()
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetProfileState() }
    }

    AirlineAppScaffold(
        companyName = stringResource(R.string.company_name),
        selectedRoute = Screen.PROFILE,
        onBottomItemClick = { /* Bottom nav handled by parent scaffold */ },
        onNotificationClick = onNavigateToNotifications
    ) { innerPadding ->
        when (val state = profileState) {
            ProfileUiState.Idle -> {
                Spacer(modifier = Modifier.padding(innerPadding))
            }
            ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Button(onClick = { viewModel.getMe() }) {
                            Text(text = "Retry")
                        }
                    }
                }
            }
            is ProfileUiState.Success -> {
                ProfileContent(
                    user = state.user,
                    innerPadding = innerPadding,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(user: User) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Spacing.mdPlus),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(Spacing.gutter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = user.fullName
                .trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
                .ifBlank { "?" }

            if (!user.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(user: User, innerPadding: PaddingValues, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.gutter)
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ProfileHeaderCard(user)

        Spacer(modifier = Modifier.height(Spacing.lg))

        SectionHeader(title = "Personal Information")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Spacing.mdPlus),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(Spacing.gutter)) {
                InfoRow(label = "Phone Number", value = user.phone ?: "Not provided")
                InfoRow(label = "Email", value = user.email)
                InfoRow(label = "Joined", value = formatDate(user.createdAt))
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        ConfirmButton(
            text = "Logout",
            onClick = onLogout,
            icon = Icons.AutoMirrored.Rounded.Logout,
            modifier = Modifier.padding(bottom = Spacing.xxl)
        )

        Text(
            text = "Version 1.0.4",
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.xl),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private fun formatDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "Not provided"
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return runCatching {
        val instant = Instant.parse(raw)
        formatter.format(instant.atZone(ZoneId.systemDefault()))
    }.getOrElse { raw }
}

