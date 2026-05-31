package com.airline.checkin.core.network

import com.airline.checkin.core.security.TokenManager
import com.airline.checkin.data.remote.dto.AuthResponseDto
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    private val gson = Gson()
    private val refreshClient = OkHttpClient.Builder().build()
    private val refreshLock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.header(NO_AUTH_HEADER) == "true") {
            val sanitized = original.newBuilder().removeHeader(NO_AUTH_HEADER).build()
            return chain.proceed(sanitized)
        }

        val accessToken = tokenManager.getAccessToken()
        val authedRequest = if (!accessToken.isNullOrBlank()) {
            original.newBuilder()
                .header(AUTH_HEADER, "Bearer $accessToken")
                .build()
        } else {
            original
        }

        val response = chain.proceed(authedRequest)
        if (response.code != 401 || original.header(RETRY_HEADER) == "true") {
            return response
        }

        val refreshedToken = refreshTokens(original.url)
        if (refreshedToken == null) {
            return response
        }

        response.close()
        val retried = authedRequest.newBuilder()
            .header(AUTH_HEADER, "Bearer $refreshedToken")
            .header(RETRY_HEADER, "true")
            .build()

        return chain.proceed(retried)
    }

    private fun refreshTokens(baseUrl: HttpUrl): String? = synchronized(refreshLock) {
        val refreshToken = tokenManager.getRefreshToken().orEmpty()
        if (refreshToken.isBlank()) {
            tokenManager.clear()
            return@synchronized null
        }

        val url = HttpUrl.Builder()
            .scheme(baseUrl.scheme)
            .host(baseUrl.host)
            .port(baseUrl.port)
            .addPathSegment("api")
            .addPathSegment("auth")
            .addPathSegment("refresh")
            .build()

        val body = gson
            .toJson(mapOf("refreshToken" to refreshToken))
            .toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = refreshClient.newCall(request).execute()
        response.use {
            if (!it.isSuccessful) {
                tokenManager.clear()
                return@synchronized null
            }

            val payload = it.body?.string().orEmpty()
            if (payload.isBlank()) {
                tokenManager.clear()
                return@synchronized null
            }

            val authResponse = runCatching { gson.fromJson(payload, AuthResponseDto::class.java) }
                .getOrNull()
                ?: return@synchronized null

            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            authResponse.accessToken
        }
    }

    private companion object {
        const val NO_AUTH_HEADER = "No-Auth"
        const val AUTH_HEADER = "Authorization"
        const val RETRY_HEADER = "X-Auth-Retry"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
