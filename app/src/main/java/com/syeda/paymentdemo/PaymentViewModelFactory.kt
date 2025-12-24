package com.syeda.paymentdemo

import PaymentViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.altapay.checkout.CheckoutPaymentClient

class PaymentViewModelFactory(
    private val paymentClient: CheckoutPaymentClient
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(paymentClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}