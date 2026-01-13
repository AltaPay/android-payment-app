package com.altapay.checkout.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthenticationResponse(
    @Expose
    @SerializedName("token")
    val token:String?
)
