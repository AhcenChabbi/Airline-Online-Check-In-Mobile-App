package com.airline.checkin.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req =
                chain.request()
                        .newBuilder()
                        // TODO: add Authorization header
                        .build()
        return chain.proceed(req)
    }
}
