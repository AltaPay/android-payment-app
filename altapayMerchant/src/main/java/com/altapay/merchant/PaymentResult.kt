package com.altapay.merchant

data class PaymentResult(
    val rawXml: String,
    val redirectUrl: String
)
