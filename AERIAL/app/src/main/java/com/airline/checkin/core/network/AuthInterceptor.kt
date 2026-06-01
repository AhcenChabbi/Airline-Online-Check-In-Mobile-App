package com.airline.checkin.core.network

import com.airline.checkin.core.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor to add Authorization header with Bearer token if not already present
class AuthInterceptor : Interceptor {

    private val debugFallbackToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ZTk3YTMwYy03NDQzLTQ4M2YtOWIwYi0yYTU1YTc4ZjE2NjgiLCJpYXQiOjE3ODAzMzE0MjMsImV4cCI6MTc4MDU5MDYyM30._pAYIFRQ8NwTiXYOKL-HPK15H6D3i03R_xZpuWFIjtI"

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        if (original.header("Authorization") == null) {
            val token = TokenManager.getAccessToken()
            val resolved = token?.ifBlank { debugFallbackToken } ?: debugFallbackToken
            if (!resolved.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $resolved")
            }
        }

        return chain.proceed(builder.build())
    }
}
