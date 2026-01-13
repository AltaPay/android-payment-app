package com.altapay.checkout.internal

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class ConditionalBasicAuthInterceptor(
    private val username: String,
    private val password: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        if (url.contains("/authentication")) {
            val auth = Credentials.basic(username, password)
            val req = chain.request().newBuilder()
                .header("Authorization", auth)
                .build()
            return chain.proceed(req)
        }

        return chain.proceed(request)
    }
}