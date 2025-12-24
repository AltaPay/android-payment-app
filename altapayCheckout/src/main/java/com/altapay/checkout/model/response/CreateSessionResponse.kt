package com.altapay.checkout.model.response

import com.altapay.checkout.model.request.Callbacks
import com.altapay.checkout.model.request.Configuration
import com.altapay.checkout.model.request.Order
import com.google.gson.annotations.SerializedName

data class CreateSessionResponse(
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("context")
    val orderContext: OrderContext,
    @SerializedName("order")
    val order: Order,
    @SerializedName("callbacks")
    val callbacks: Callbacks,
    @SerializedName("configuration")
    val configuration: Configuration
)

data class OrderContext(
    val browser: Browser
)

data class Browser(
    val test: String
)