package com.altapay.checkout.internal

internal object SessionConfig {
    private var _authToken: String? = null

    val authToken: String?
        get() = _authToken

    fun setAuthToken(token: String?) {
        _authToken = token
    }

    fun clear() {
        _authToken = null
    }
}