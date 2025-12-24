package com.altapay.checkout.internal

import com.altapay.checkout.model.request.Callbacks
import com.altapay.checkout.model.request.CreatePaymentRequest
import com.altapay.checkout.model.request.CreateSessionRequest
import com.altapay.checkout.model.response.AuthenticationResponse
import com.altapay.checkout.model.response.CreatePaymentResponse
import com.altapay.checkout.model.response.CreateSessionResponse
import com.altapay.checkout.model.response.PaymentMethodResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

internal interface PaymentApi {

    @FormUrlEncoded
    @POST
    suspend fun authenticate(
        @Url url: String,
        @FieldMap params: Map<String, String>
    ): AuthenticationResponse


    @POST("checkout/v1/api/session")
    suspend fun createSession(
        @Body body: CreateSessionRequest
    ): CreateSessionResponse

    @GET
    suspend fun getPaymentMethods(
        @Url url: String,
    ): PaymentMethodResponse

    @POST("checkout/v1/api/payment")
    suspend fun createPayment(
        @Body body: CreatePaymentRequest
    ) :  CreatePaymentResponse
}