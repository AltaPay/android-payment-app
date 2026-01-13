package com.altapay.checkout.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @Expose
    @SerializedName("id") var id: String? = null,
    @Expose
    @SerializedName("type") var type: String? = null,
    @Expose
    @SerializedName("description") var description: String? = null,
    @Expose
    @SerializedName("logoUrl") var logoUrl: String? = null,
    @Expose
    @SerializedName("display") var display: String? = null,
    @Expose
    @SerializedName("onInitiatePayment") var onInitiatePayment: OnInitiatePayment? = OnInitiatePayment(),
    @Expose
    @SerializedName("metadata") var metadata: Metadata? = Metadata(),
    @Expose
    @SerializedName("name") var name: String? = null

)