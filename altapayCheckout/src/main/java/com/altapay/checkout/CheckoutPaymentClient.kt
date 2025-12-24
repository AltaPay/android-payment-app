package com.altapay.checkout

import com.altapay.checkout.internal.PaymentRepository
import com.altapay.checkout.internal.RetrofitFactory
import com.altapay.checkout.model.PaymentConfig
import com.altapay.checkout.model.request.CreatePaymentRequest
import com.altapay.checkout.model.request.CreateSessionRequest
import com.altapay.checkout.model.response.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class CheckoutPaymentClient(private val config: PaymentConfig) {
    private val api = RetrofitFactory.create(config)
    private val repo = PaymentRepository(api)
    private val _paymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val paymentMethod: StateFlow<PaymentMethod?> get() = _paymentMethod

    private val _availablePaymentMethods = MutableStateFlow<List<PaymentMethod>?>(null)
    val availablePaymentMethods: StateFlow<List<PaymentMethod>?> get() = _availablePaymentMethods

    private fun validate(): PaymentError.InvalidConfig? {
        if (config.username.isBlank()) return PaymentError.InvalidConfig("Username is blank")
        if (config.password.isBlank()) return PaymentError.InvalidConfig("Password is blank")
        return null
    }

    // This method only fetches available payment methods, doesn't select one
    suspend fun fetchPaymentMethods(sessionId: String): List<PaymentMethod>? {
        val getPaymentMethodsUrl =
            "${config.baseUrl}checkout/v1/api/session/$sessionId/payment-methods"

        when (val result = repo.getPaymentMethods(getPaymentMethodsUrl)) {
            is PaymentRepository.Result.Error -> {
                return null
            }

            is PaymentRepository.Result.Success -> {
                _availablePaymentMethods.value = result.data.methods
                return result.data.methods
            }
        }
    }

    // Let client application call this when user selects a payment method
    fun selectPaymentMethod(method: PaymentMethod) {
        _paymentMethod.value = method
    }

    suspend fun authenticateAndCreateSession(
        sessionRequest: CreateSessionRequest
    ): Result<String> { // Returns sessionId or error
        // Authenticate
        return when (val authResult = repo.authenticate(
            config.baseUrl + "checkout/v1/api/authenticate",
            mapOf("userName" to config.username, "password" to config.password)
        )) {
            is PaymentRepository.Result.Error -> Result.failure(Exception(authResult.error.message))
            is PaymentRepository.Result.Success -> {
                // Create session
                when (val sessionResult = repo.createSession(sessionRequest)) {
                    is PaymentRepository.Result.Error -> Result.failure(Exception(sessionResult.error.message))
                    is PaymentRepository.Result.Success -> {
                        Result.success(sessionResult.data.sessionId)
                    }
                }
            }
        }
    }

    suspend fun createPayment(
        sessionId: String
    ): PaymentResultCheckout {
        val selectedMethod = _paymentMethod.value
            ?: return PaymentResultCheckout.Error(PaymentError.InvalidConfig("No payment method selected"))

        val createPaymentRequest = CreatePaymentRequest(
            selectedMethod.id ?: "",
            sessionId
        )

        when (val paymentResult = repo.createPayment(createPaymentRequest)) {
            is PaymentRepository.Result.Error -> return PaymentResultCheckout.Error(paymentResult.error)
            is PaymentRepository.Result.Success -> {
                return PaymentResultCheckout.Success(paymentResult.data.url)
            }
        }
    }

    suspend fun waitForPaymentMethodSelection(): PaymentMethod? {
        return withContext(Dispatchers.Main) {
            while (_paymentMethod.value == null) {
                delay(100)
            }
            _paymentMethod.value
        }
    }
}
