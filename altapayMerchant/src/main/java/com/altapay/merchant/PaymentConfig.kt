package com.altapay.merchant

data class PaymentConfig(
    val username: String,
    val password: String,

    // Networking
    val connectTimeoutSec: Long = 20,
    val readTimeoutSec: Long = 30,
    val writeTimeoutSec: Long = 30,

    // Retry (idempotency warning: payment creation may not be safe to retry blindly)
    val enableRetry: Boolean = true,
    val maxRetries: Int = 1,

    // Logging (host app may disable in prod)
    val enableHttpLogging: Boolean = false,

    // Optional TLS pinning (recommended for payments if you control the endpoint cert chain)
    val tlsPinning: TlsPinningConfig? = null
)

data class TlsPinningConfig(
    val hostname: String,

    val sha256Pins: List<String>
)
