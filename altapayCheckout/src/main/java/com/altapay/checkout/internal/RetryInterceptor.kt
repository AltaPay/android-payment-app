package com.altapay.checkout.internal

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * WARNING: Retrying payment creation can create duplicates depending on gateway behavior.
 * Keep maxRetries low and consider using an idempotency key if the gateway supports it.
 */
internal class RetryInterceptor(
    private val enableRetry: Boolean,
    private val maxRetries: Int
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var lastException: IOException? = null

        while (true) {
            try {
                val response = chain.proceed(chain.request())
                // Retry on transient 5xx only
                if (enableRetry && response.code in 500..599 && attempt < maxRetries) {
                    response.close()
                    attempt++
                    continue
                }
                return response
            } catch (e: IOException) {
                lastException = e
                if (!enableRetry || attempt >= maxRetries) throw e
                attempt++
            }
        }
    }
}
