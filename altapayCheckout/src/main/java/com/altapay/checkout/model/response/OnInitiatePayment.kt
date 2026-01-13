package com.altapay.checkout.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OnInitiatePayment(
    @Expose
    @SerializedName("type") var type: String? = null,
    @Expose
    @SerializedName("value") var value: String? = null
)