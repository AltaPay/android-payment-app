package com.altapay.checkout.internal

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class FullRequestLoggerInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        logRequest(request)

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            println("NETWORK ERROR: ${e.message}")
            throw e
        }

        logResponse(response)
        return response
    }

    private fun logRequest(request: okhttp3.Request) {
        println("\n══════════════════════════════════════════════════════════")
        println("REQUEST:")
        println("URL: ${request.method} ${request.url}")
        println("Headers:")

        // CORRECTED: Use proper iteration for headers
        for (i in 0 until request.headers.size) {
            val name = request.headers.name(i)
            val value = request.headers.value(i)
            println("  $name: $value")
        }

        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            val bodyString = buffer.readUtf8()
            if (bodyString.isNotEmpty()) {
                println("Body: $bodyString")
            }
        } catch (e: IOException) {
            println("Could not read request body: ${e.message}")
        }
        println("══════════════════════════════════════════════════════════\n")
    }

    private fun logResponse(response: Response) {
        println("\n══════════════════════════════════════════════════════════")
        println("RESPONSE:")
        println("URL: ${response.request.url}")
        println("Status: ${response.code} ${response.message}")
        println("Headers:")

        // CORRECTED: Use proper iteration for headers
        for (i in 0 until response.headers.size) {
            val name = response.headers.name(i)
            val value = response.headers.value(i)
            println("  $name: $value")
        }

        try {
            val responseBody = response.peekBody(1024 * 1024) // 1MB
            val bodyString = responseBody.string()
            if (bodyString.isNotEmpty()) {
                println("Body (first 1MB): $bodyString")
            }
        } catch (e: IOException) {
            println("Could not read response body: ${e.message}")
        }
        println("══════════════════════════════════════════════════════════\n")
    }
}