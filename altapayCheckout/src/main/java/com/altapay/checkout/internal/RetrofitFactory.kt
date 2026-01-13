package com.altapay.checkout.internal

import ContentTypeInterceptor
import com.altapay.checkout.model.PaymentConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

internal object RetrofitFactory {

    fun create(config: PaymentConfig): PaymentApi {
        val gson: Gson = createGsonWithKotlinSupport()

        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSec, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSec, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSec, TimeUnit.SECONDS)
            .addInterceptor(BasicAuthInterceptor(config.username, config.password))
            .addInterceptor(ContentTypeInterceptor())  // Add Content-Type
            .addInterceptor(AuthTokenInterceptor())
            .addInterceptor(RetryInterceptor(config.enableRetry, config.maxRetries))
            .addInterceptor(FullRequestLoggerInterceptor())



        if (config.enableHttpLogging) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        config.tlsPinning?.let { pin ->
            val pinner = CertificatePinner.Builder().apply {
                pin.sha256Pins.forEach { add(pin.hostname, it) }
            }.build()
            builder.certificatePinner(pinner)
        }

        val client = builder.build()

        return Retrofit.Builder()
            .baseUrl(config.baseUrl)  // Required but overridden by @Url
            .client(client)
            // IMPORTANT: Order matters! Scalars first, then Gson
            .addConverterFactory(ScalarsConverterFactory.create())      // For String/primitive responses
            .addConverterFactory(GsonConverterFactory.create(gson))     // For JSON responses
            .build()
            .create(PaymentApi::class.java)
    }

    private fun createGsonWithKotlinSupport(): Gson {
        return GsonBuilder()
            .setLenient()
            .registerTypeAdapterFactory(KotlinTypeAdapterFactory())  // Add Kotlin support
            .serializeNulls()  // Optional: include null values in JSON
            .create()
    }


}

class KotlinTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType

        if (rawType.name.contains("kotlin.Metadata") ||
            rawType.name.contains("kotlin.jvm.internal")
        ) {
            return EmptyTypeAdapter()
        }

        return null
    }

    private class EmptyTypeAdapter<T> : TypeAdapter<T>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: T) {
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): T? {
            reader.skipValue()
            return null
        }
    }
}