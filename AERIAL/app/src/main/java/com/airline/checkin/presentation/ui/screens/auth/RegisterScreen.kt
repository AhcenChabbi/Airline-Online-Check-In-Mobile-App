package com.airline.checkin.presentation.ui.screens.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airline.checkin.R
import com.airline.checkin.presentation.ui.components.AeroAuthTextField
import com.airline.checkin.presentation.ui.components.AeroPasswordTextField
import com.airline.checkin.presentation.ui.components.AuthBackgroundCard
import com.airline.checkin.presentation.ui.components.GoldButton
import com.airline.checkin.presentation.ui.components.LoadingOverlay
import com.airline.checkin.presentation.ui.state.AuthUiState
import com.airline.checkin.presentation.ui.viewmodels.AuthViewModel
import com.airline.checkin.presentation.ui.theme.Spacing

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    
    var nameErrorRes by remember { mutableStateOf<Int?>(null) }
    var emailErrorRes by remember { mutableStateOf<Int?>(null) }
    var phoneErrorRes by remember { mutableStateOf<Int?>(null) }
    var passwordErrorRes by remember { mutableStateOf<Int?>(null) }
    var confirmPasswordErrorRes by remember { mutableStateOf<Int?>(null) }

    val isFormValid = name.isNotBlank() && email.isNotBlank() &&
                      password.isNotBlank() && confirmPassword.isNotBlank() && termsAccepted

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    fun submitRegister() {
        nameErrorRes = validateFullName(name)
        phoneErrorRes = validatePhone(phone)
        emailErrorRes = validateEmail(email)
        passwordErrorRes = validatePassword(password)
        confirmPasswordErrorRes = if (password != confirmPassword) R.string.error_password_mismatch else null

        if (nameErrorRes == null &&
            phoneErrorRes == null &&
            emailErrorRes == null &&
            passwordErrorRes == null &&
            confirmPasswordErrorRes == null &&
            termsAccepted
        ) {
            viewModel.register(
                fullName = name,
                email = email,
                phone = phone.trim().ifBlank { null },
                password = password
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackgroundCard {
            // Title Text
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Join Skyward Premium for a seamless travel experience.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Full Name field
            AeroAuthTextField(
                label = "Full Name",
                placeholder = "Enter your name",
                value = name,
                onValueChange = {
                    name = it
                    nameErrorRes = null
                },
                leadingIcon = Icons.Rounded.Person,
                error = nameErrorRes?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Email field
            AeroAuthTextField(
                label = "Email Address",
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

            // Phone field
            AeroAuthTextField(
                label = "Phone Number",
                placeholder = "Enter your phone number (optional)",
                value = phone,
                onValueChange = {
                    phone = it
                    phoneErrorRes = null
                },
                leadingIcon = Icons.Rounded.Phone,
                error = phoneErrorRes?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Password field
            AeroPasswordTextField(
                label = "Password",
                placeholder = "Create a password",
                value = password,
                onValueChange = {
                    password = it
                    passwordErrorRes = null
                },
                leadingIcon = Icons.Rounded.Lock,
                error = passwordErrorRes?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Confirm Password field
            AeroPasswordTextField(
                label = "Confirm Password",
                placeholder = "Confirm your password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordErrorRes = null
                },
                leadingIcon = Icons.Rounded.Lock,
                error = confirmPasswordErrorRes?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Terms and Conditions View
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.secondaryContainer,
                        uncheckedColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Text(
                    text = "I agree to the ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Terms & Conditions",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { /* TODO: Show Terms */ }
                )
            }

            val errorMessage = (uiState as? AuthUiState.Error)?.message
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            } else {
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            // Sign Up Button
            GoldButton(
                text = "Sign Up",
                onClick = ::submitRegister,
                enabled = isFormValid
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Footer Text
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { onBack() }
                )
            }
        }

        if (uiState is AuthUiState.Loading) {
            LoadingOverlay()
        }
    }
}

@StringRes
private fun validateFullName(name: String): Int? {
    val trimmed = name.trim()
    return when {
        trimmed.isEmpty() -> R.string.error_full_name_required
        trimmed.length < 2 -> R.string.error_full_name_invalid
        else -> null
    }
}

@StringRes
private fun validatePhone(phone: String): Int? {
    val normalized = phone.filter { it.isDigit() }
    return when {
        phone.trim().isEmpty() -> null
        normalized.length !in 8..15 -> R.string.error_phone_invalid
        else -> null
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
        trimmed.length < 8 -> R.string.error_password_short
        else -> null
    }
}
