package com.altapay.merchant.internal

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

internal class BasicAuthInterceptor(
    private val username: String,
    private val password: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val auth = Credentials.basic(username, password)
        val req = chain.request().newBuilder()
            .header("Authorization", auth)
            .build()
        return chain.proceed(req)
    }
}
