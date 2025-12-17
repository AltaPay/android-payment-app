package com.altapay.merchant

interface PaymentCallback {
    fun onSuccess(result: PaymentResult)
    fun onError(error: PaymentError)
}