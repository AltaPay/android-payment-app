package com.altapay.checkout.internal

import okhttp3.Interceptor
import okhttp3.Response

class HeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()

        requestBuilder
            .removeHeader("User-Agent")  // Remove Android's User-Agent
            .removeHeader("Accept-Encoding")  // Remove OkHttp's default

        // Add Postman headers
        requestBuilder
            .addHeader("Accept", "*/*")
            .addHeader("Accept-Encoding", "gzip, deflate, br")
            .addHeader("Connection", "keep-alive")
            .addHeader("User-Agent", "")  // Match Postman exactly

        // Ensure Content-Type is set for POST requests
        if (originalRequest.method == "POST" && originalRequest.body != null) {
            val contentType = originalRequest.header("Content-Type")
            if (contentType == null) {
                requestBuilder.addHeader("Content-Type", "application/json")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}