package com.altapay.checkout.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateSessionRequest(
    @SerializedName("order")
    @Expose
    val order: Order,

    @SerializedName("callbacks")
    @Expose
    val callbacks: Callbacks? = null,

    @SerializedName("configuration")
    @Expose
    val configuration: Configuration
)

data class Order(
    @SerializedName("orderId")
    @Expose
    val orderId: String,

    @SerializedName("amount")
    @Expose
    val amount: Amount,

    @SerializedName("orderLines")
    @Expose
    val orderLines: List<OrderLine>,

    @SerializedName("customer")
    @Expose
    val customer: Customer,

    @SerializedName("transactionInfo")
    @Expose
    val transactionInfo: TransactionInfo
)

// Amount
data class Amount(
    @SerializedName("value")
    @Expose
    val value: Double,

    @SerializedName("currency")
    @Expose
    val currency: String
)

// Order Line
data class OrderLine(
    @SerializedName("itemId")
    @Expose
    val itemId: String,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("quantity")
    @Expose
    val quantity: Int,

    @SerializedName("unitPrice")
    @Expose
    val unitPrice: Double
)

// Customer
data class Customer(
    @SerializedName("firstName")
    @Expose
    val firstName: String,

    @SerializedName("lastName")
    @Expose
    val lastName: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("billingAddress")
    @Expose
    val billingAddress: Address,

    @SerializedName("shippingAddress")
    @Expose
    val shippingAddress: Address
)

// Address
data class Address(
    @SerializedName("street")
    @Expose
    val street: String,

    @SerializedName("city")
    @Expose
    val city: String,

    @SerializedName("country")
    @Expose
    val country: String,

    @SerializedName("zipCode")
    @Expose
    val zipCode: String
)

// Transaction Info
data class TransactionInfo(
    @SerializedName("testparam1")
    @Expose
    val testParam1: String,

    @SerializedName("testparam2")
    @Expose
    val testParam2: String,

    @SerializedName("testparam3")
    @Expose
    val testParam3: String
)

data class Callbacks(
    @SerializedName("formStyling")
    @Expose
    val formStyling: String? = null,

    @SerializedName("success")
    @Expose
    val success: CallbackTarget? = null,

    @SerializedName("failure")
    @Expose
    val failure: CallbackTarget? = null,

    @SerializedName("redirect")
    @Expose
    val redirect: String? = null,

    @SerializedName("notification")
    @Expose
    val notification: String? = null
)

data class CallbackTarget(
    @SerializedName("type")
    @Expose
    val type: String,

    @SerializedName("value")
    @Expose
    val value: String
)


// Configuration
data class Configuration(
    @SerializedName("paymentType")
    @Expose
    val paymentType: String,

    @SerializedName("bodyFormat")
    @Expose
    val bodyFormat: String,

    @SerializedName("autoCapture")
    @Expose
    val autoCapture: Boolean,

    @SerializedName("paymentDisplayType")
    @Expose
    val paymentDisplayType: String,

    @SerializedName("country")
    @Expose
    val country: String,

    @SerializedName("language")
    @Expose
    val language: String
)