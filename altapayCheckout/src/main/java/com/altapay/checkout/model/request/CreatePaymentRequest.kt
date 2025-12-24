package com.altapay.checkout.model.request

data class CreatePaymentRequest(
    val paymentMethodId: String,
    val sessionId: String
)