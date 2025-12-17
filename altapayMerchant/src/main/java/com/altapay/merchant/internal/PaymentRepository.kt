package com.altapay.merchant.internal


internal class PaymentRepository(
    private val api: PaymentApi
) {
    suspend fun createPayment(url: String, params: Map<String, String>): String {
        return api.createPayment(url, params)
    }
}
