package com.altapay.checkout

sealed class PaymentResultCheckout {
    data class Success(val redirectUrl: String) : PaymentResultCheckout()
    data class Error(val error: PaymentError) : PaymentResultCheckout()
}