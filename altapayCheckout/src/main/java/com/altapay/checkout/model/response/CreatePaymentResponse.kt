package com.altapay.checkout.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreatePaymentResponse(
    @Expose
    @SerializedName("paymentId")
    val paymentId: String,
    @Expose
    @SerializedName("shopOrderId")
    val shopOrderId: String,
    @Expose
    @SerializedName("status")
    val status: String,
    @Expose
    @SerializedName("type")
    val type: String,
    @Expose
    @SerializedName("url")
    val url: String,
)
