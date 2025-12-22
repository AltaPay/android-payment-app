package com.altapay.merchant

sealed class PaymentError(open val message: String, open val cause: Throwable? = null) {
    data class Network(override val message: String, override val cause: Throwable? = null) : PaymentError(message, cause)
    data class Parse(override val message: String, override val cause: Throwable? = null) : PaymentError(message, cause)
    data class InvalidConfig(override val message: String) : PaymentError(message, null)
    data class Unknown(override val message: String, override val cause: Throwable? = null) : PaymentError(message, cause)
}
