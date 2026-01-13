package com.altapay.checkout.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentMethodResponse(
    @Expose
    @SerializedName("methods") var methods: ArrayList<PaymentMethod> = arrayListOf()
)