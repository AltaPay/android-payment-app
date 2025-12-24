package com.altapay.checkout

import com.altapay.checkout.model.response.PaymentMethod

interface PaymentCallback {
    fun onSuccess(result: PaymentResultCheckout.Success)
    fun onError(error: PaymentError)
    fun onPaymentMethodGenerated(method: List<PaymentMethod>)
}