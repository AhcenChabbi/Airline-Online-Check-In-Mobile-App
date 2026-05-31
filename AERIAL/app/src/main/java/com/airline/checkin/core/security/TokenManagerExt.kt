package com.airline.checkin.core.security

fun TokenManager.isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

