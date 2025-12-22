package com.altapay.merchant.internal

import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

internal interface PaymentApi {
    @FormUrlEncoded
    @POST
    suspend fun createPayment(
        @Url url: String,
        @FieldMap params: Map<String, String>
    ): String
}