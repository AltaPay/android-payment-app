package com.altapay.merchant.internal

import com.altapay.merchant.PaymentConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

internal object RetrofitFactory {

    fun create(config: PaymentConfig): PaymentApi {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSec, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSec, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSec, TimeUnit.SECONDS)
            .addInterceptor(BasicAuthInterceptor(config.username, config.password))
            .addInterceptor(RetryInterceptor(config.enableRetry, config.maxRetries))

        if (config.enableHttpLogging) {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            builder.addInterceptor(logging)
        }

        config.tlsPinning?.let { pin ->
            val pinner = CertificatePinner.Builder().apply {
                pin.sha256Pins.forEach { add(pin.hostname, it) }
            }.build()
            builder.certificatePinner(pinner)
        }

        val client = builder.build()

        // baseUrl is required; @Url overrides it
        return Retrofit.Builder()
            .baseUrl("https://placeholder.invalid/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(PaymentApi::class.java)
    }
}
