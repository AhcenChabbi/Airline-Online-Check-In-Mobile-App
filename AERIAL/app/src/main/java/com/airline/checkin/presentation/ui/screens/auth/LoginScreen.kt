package com.airline.checkin.presentation.ui.screens.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airline.checkin.R
import com.airline.checkin.presentation.ui.components.AeroAuthTextField
import com.airline.checkin.presentation.ui.components.AeroPasswordTextField
import com.airline.checkin.presentation.ui.components.AuthBackgroundCard
import com.airline.checkin.presentation.ui.components.GoldButton
import com.airline.checkin.presentation.ui.components.GoogleSignInButton
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailErrorRes by remember { mutableStateOf<Int?>(null) }
    var passwordErrorRes by remember { mutableStateOf<Int?>(null) }

    val isFormValid = email.isNotBlank() && password.isNotBlank()

    fun submitLogin() {
        emailErrorRes = validateEmail(email)
        passwordErrorRes = validatePassword(password)

        if (emailErrorRes == null && passwordErrorRes == null) {
            onLoginSuccess()
        }
    }

    AuthBackgroundCard {
        // Welcome Text
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primaryContainer
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = "Login to your account to manage your trips.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Email field
        AeroAuthTextField(
            label = "Email address",
            placeholder = "Enter your email",
            value = email,
            onValueChange = {
                email = it
                emailErrorRes = null
            },
            leadingIcon = Icons.Rounded.Email,
            error = emailErrorRes?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Password field
        AeroPasswordTextField(
            label = "Password",
            placeholder = "Enter your password",
            value = password,
            onValueChange = {
                password = it
                passwordErrorRes = null
            },
            leadingIcon = Icons.Rounded.Lock,
            error = passwordErrorRes?.let { stringResource(it) }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Forgot Password Action
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clickable { /* TODO: forgot password */ }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Login Button
        GoldButton(
            text = "Login",
            onClick = ::submitLogin,
            enabled = isFormValid
        )

        // Or Divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = Spacing.mdPlus)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "or",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }

        // Google Sign In
        GoogleSignInButton(onClick = { /* TODO: trigger OAuth flow */ })

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Footer Text
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

@StringRes
private fun validateEmail(email: String): Int? {
    val trimmed = email.trim()
    return when {
        trimmed.isEmpty() -> R.string.error_email_required
        !Patterns.EMAIL_ADDRESS.matcher(trimmed).matches() -> R.string.error_email_invalid
        else -> null
    }
}

@StringRes
private fun validatePassword(password: String): Int? {
    val trimmed = password.trim()
    return when {
        trimmed.isEmpty() -> R.string.error_password_required
        trimmed.length < 6 -> R.string.error_password_short
        else -> null
    }
}
