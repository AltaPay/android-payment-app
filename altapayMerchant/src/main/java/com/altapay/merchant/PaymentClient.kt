package com.altapay.merchant


import com.altapay.merchant.internal.PaymentRepository
import com.altapay.merchant.internal.PaymentXmlParser
import com.altapay.merchant.internal.RetrofitFactory
import kotlinx.coroutines.*

class PaymentClient(private val config: PaymentConfig) {

    private val api = RetrofitFactory.create(config)
    private val repo = PaymentRepository(api)

    private fun validate(): PaymentError.InvalidConfig? {
        if (config.username.isBlank()) return PaymentError.InvalidConfig("Username is blank")
        if (config.password.isBlank()) return PaymentError.InvalidConfig("Password is blank")
        return null
    }

    /**
     * Kotlin-first suspend API (recommended).
     */
    suspend fun createPayment(
        paymentUrl: String,
        params: Map<String, String>
    ): Result<PaymentResult> {
        validate()?.let { return Result.failure(IllegalArgumentException(it.message)) }

        return try {
            val xml = repo.createPayment(paymentUrl, params)
            val redirect = PaymentXmlParser.extractRedirectUrl(xml)
                ?: return Result.failure(IllegalStateException("Payment URL not found in XML response"))
            Result.success(PaymentResult(rawXml = xml, redirectUrl = redirect))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Java-friendly callback API.
     * Library does not require lifecycle. Host app may cancel by holding the returned Job.
     */
    fun createPaymentAsync(
        paymentUrl: String,
        params: Map<String, String>,
        callback: PaymentCallback,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Job {
        validate()?.let {
            callback.onError(it)
            return Job().apply { complete() }
        }

        return CoroutineScope(SupervisorJob() + dispatcher).launch {
            try {
                val xml = repo.createPayment(paymentUrl, params)
                val redirect = PaymentXmlParser.extractRedirectUrl(xml)
                    ?: throw IllegalStateException("Payment URL not found in XML response")

                withContext(Dispatchers.Main) {
                    callback.onSuccess(PaymentResult(xml, redirect))
                }
            } catch (e: Exception) {
                val err = when (e) {
                    is IllegalStateException -> PaymentError.Parse(e.message ?: "Parse error", e)
                    else -> PaymentError.Network(e.message ?: "Network error", e)
                }
                withContext(Dispatchers.Main) { callback.onError(err) }
            }
        }
    }
}
