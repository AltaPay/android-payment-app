package com.altapay.checkout.internal

import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authToken = SessionConfig.authToken

        val request = chain.request()
        val url = request.url.toString()

        println("AuthTokenInterceptor - Token available: ${!authToken.isNullOrEmpty()}")
        println("AuthTokenInterceptor - Request URL: ${originalRequest.url}")

        val requestBuilder = originalRequest.newBuilder()

        if (!authToken.isNullOrEmpty() && !url.contains("/authenticate") ) {
            // Add Authorization header
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
            println("AuthTokenInterceptor - Added Authorization header")
        } else {
            println("AuthTokenInterceptor - No token available")
        }

        return chain.proceed(requestBuilder.build())
    }
}