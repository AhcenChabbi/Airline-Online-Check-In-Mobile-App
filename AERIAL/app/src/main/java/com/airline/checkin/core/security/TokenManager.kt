package com.airline.checkin.core.security

import android.content.Context

object TokenManager {
    private const val KEY_ACCESS = "access_token"
    private const val KEY_REFRESH = "refresh_token"

    fun init(context: Context) {
        SecureStorage.init(context)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        SecureStorage.putString(KEY_ACCESS, accessToken)
        SecureStorage.putString(KEY_REFRESH, refreshToken)
    }

    fun getAccessToken(): String? = SecureStorage.getString(KEY_ACCESS)

    fun getRefreshToken(): String? = SecureStorage.getString(KEY_REFRESH)

    fun clear() {
        SecureStorage.putString(KEY_ACCESS, null)
        SecureStorage.putString(KEY_REFRESH, null)
    }
}
