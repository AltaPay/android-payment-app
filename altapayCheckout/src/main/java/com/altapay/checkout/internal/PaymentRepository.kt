package com.altapay.checkout.internal

import com.altapay.checkout.PaymentError
import com.altapay.checkout.model.request.CreatePaymentRequest
import com.altapay.checkout.model.request.CreateSessionRequest
import com.altapay.checkout.model.response.AuthenticationResponse
import com.altapay.checkout.model.response.CreatePaymentResponse
import com.altapay.checkout.model.response.CreateSessionResponse
import com.altapay.checkout.model.response.PaymentMethodResponse


internal class PaymentRepository(
    private val api: PaymentApi
) {

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val error: PaymentError) : Result<Nothing>()
    }

    suspend fun authenticate(
        url: String,
        params: Map<String, String>
    ): Result<AuthenticationResponse> {
        return try {
            val response = api.authenticate(url, params)
            if (!response.token.isNullOrEmpty()) {
                SessionConfig.setAuthToken(response.token)
                println("Token stored in SessionConfig: ${response.token.take(20)}...")
            }
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(PaymentError.Network("Authentication failed", e))
        }
    }

    suspend fun createSession(createSessionRequest: CreateSessionRequest): Result<CreateSessionResponse> {
        return try {
            val response = api.createSession(createSessionRequest)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(PaymentError.Network("Failed to create session", e))
        }
    }

    suspend fun getPaymentMethods(url: String): Result<PaymentMethodResponse> {
        return try {
            val response = api.getPaymentMethods(url)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(PaymentError.Network("Failed to fetch payment methods", e))
        }
    }


    suspend fun createPayment(
        createPaymentRequest: CreatePaymentRequest
    ): Result<CreatePaymentResponse> {
        return try {
            val response = api.createPayment(createPaymentRequest)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(PaymentError.Network("Failed to create payment", e))
        }
    }
}
