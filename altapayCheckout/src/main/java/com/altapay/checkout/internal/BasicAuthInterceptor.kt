package com.altapay.checkout.internal

import android.util.Base64
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(private val username: String, private val password: String) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        // Check if this is the authentication endpoint
        val isAuthenticationEndpoint = url.contains("/authenticate")

        if (isAuthenticationEndpoint) {
            println("BasicAuthInterceptor - Adding Basic Auth for authentication endpoint")

            val credentials = "$username:$password"
            val base64Credentials = Base64.encodeToString(
                credentials.toByteArray(),
                Base64.NO_WRAP
            )


            val newRequest = request.newBuilder()
                .removeHeader("Authorization")  // Remove any existing Authorization header
                .addHeader("Authorization", "Basic $base64Credentials")
                .build()

            return chain.proceed(newRequest)
        }

        // For all other endpoints, remove Basic Auth header if present
        println("BasicAuthInterceptor - Skipping for non-auth endpoint: $url")
        val newRequest = request.newBuilder()
            .removeHeader("Authorization")  // Remove Basic Auth header
            .build()

        return chain.proceed(newRequest)
    }
}