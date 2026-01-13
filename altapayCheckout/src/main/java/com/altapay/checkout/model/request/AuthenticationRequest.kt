package com.altapay.checkout.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthenticationRequest(
    @Expose
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
)
