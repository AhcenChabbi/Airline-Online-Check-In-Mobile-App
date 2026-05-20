package com.airline.checkin.core.network

import com.airline.checkin.core.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor to add Authorization header with Bearer token if not already present
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        if (original.header("Authorization") == null) {
            val token = TokenManager.getAccessToken()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(builder.build())
    }
}
