# AltaPay Android SDK – Integration Wiki

This document explains how to integrate AltaPay payments into an Android application using the provided SDKs.  
It covers **project setup**, **demo app usage**, and **two integration approaches**:

- **Merchant API(`altapayMerchant`)**
- **Checkout API (`altapayCheckout`)**
- - [Import Cartridge](#import-cartridge)

## How to use

### Clone the Repository

```bash
git clone https://github.com/AltaPay/android-payment-app.git
cd android-payment-app
```

### Open in Android Studio
```
File > Open > select android-payment-app
```
Wait for **Gradle sync** to complete.

## Running the Demo App

1. Select the **app** run configuration  
2. Connect a device or start an emulator  
3. Click **Run**

### Demo App Configuration

```kotlin
paymentClient = PaymentClient(
    PaymentConfig(
        username = "YOUR_API_USERNAME",
        password = "YOUR_API_PASSWORD"
    )
)
```

> Do not hardcode credentials in production apps.


## Required Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Request Parameters

| Parameter      | Description       |
|----------------|-------------------|
| terminal       | Merchant terminal |
| shop_orderid   | Order ID          |
| amount         | Payment amount    |
| currency       | Currency          |
| otherparameter | value             |

## Merchant SDK Integration (`altapayMerchant`)

### Add SDK Module

```gradle
include(":altapayMerchant")
```

```gradle
dependencies {
    implementation project(":altapayMerchant")
}
```

### Initialize Client

```kotlin
val paymentClient = PaymentClient(
    PaymentConfig(
        username = "YOUR_API_USERNAME",
        password = "YOUR_API_PASSWORD"
    )
)
```

### Create Payment

```kotlin
paymentClient.createPaymentAsync(
    paymentUrl = "https://gateway.altapay.com/payment",
    params = getPaymentParams(),
    callback = object : PaymentCallback { }
)
```

## Checkout SDK Integration (`altapayCheckout`)

```gradle
dependencies {
    implementation project(":altapayCheckout")
}
```

```kotlin
val paymentClient = CheckoutPaymentClient(paymentConfig)
```

### Configure the Payment Client

Before starting the payment process, configure the payment client by providing the necessary API credentials and base URL.

```kotlin
val paymentConfig = PaymentConfig(
    username = "your-username",        // Your API username
    password = "your-password",        // Your API password
    baseUrl = "https://testgateway.altapaysecure.com/"  // Base URL for the payment gateway
)
val paymentClient = CheckoutPaymentClient(paymentConfig)
```

### Create a ViewModel

Create a `ViewModel` to manage the payment state and operations. This ViewModel will interact with the `CheckoutPaymentClient`.

```kotlin
class PaymentViewModel(private val paymentClient: CheckoutPaymentClient) : ViewModel() {
    private val _paymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val paymentMethod: StateFlow<PaymentMethod?> get() = _paymentMethod

    private val _availablePaymentMethods = MutableStateFlow<List<PaymentMethod>?>(null)
    val availablePaymentMethods: StateFlow<List<PaymentMethod>?> get() = _availablePaymentMethods

    private val _paymentResult = MutableStateFlow<PaymentResultCheckout?>(null)
    val paymentResult: StateFlow<PaymentResultCheckout?> get() = _paymentResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState> get() = _loadingState

    // Add methods to interact with the CheckoutPaymentClient here
}
```
### Start the Payment Flow

To initiate the payment process, follow these steps:

#### Step 1: Authenticate and Create a Session
Authenticate the user and create a session.

```kotlin
val sessionRequest = CreateSessionRequest(
    // Provide order details like order ID, amount, customer information, etc.
)

lifecycleScope.launch {
    viewModel.authenticateAndCreateSession(sessionRequest)
}
```

#### Step 2: Fetch Available Payment Methods
Once the session is created, fetch available payment methods.

```kotlin
viewModel.fetchPaymentMethods(sessionId)
```

#### Step 3: Select a Payment Method
After fetching payment methods, let the user select one.

```kotlin
viewModel.selectPaymentMethod(selectedMethod)
```

#### Step 4: Create the Payment
Once the user selects a payment method, proceed with creating the payment.

```kotlin
viewModel.createPayment()
```

### UI Integration

#### Payment Method UI Customization
The UI flow is flexible and customizable. You can display payment methods in different formats:

- **Dialog**: A simple dialog for the user to select a payment method.
- **RecyclerView**: A list or grid view displaying payment methods.
- **Custom UI**: Custom UI components for payment method selection.

Once the user selects a payment method, call `viewModel.selectPaymentMethod(selectedMethod)`.

#### WebView for Payment Completion
After the payment is created, you can use a WebView to load the payment URL and process the payment.

```kotlin
// Load the payment URL in WebView for completing the payment
webView.loadUrl(paymentUrl)
```

#### Handling Payment Completion
Monitor the WebView’s state to detect successful payment completion, failure, or cancellation.

```kotlin
webView.webViewClient = object : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url.contains("payment-success")) {
            // Handle success
        } else if (url.contains("payment-failed")) {
            // Handle failure
        } else if (url.contains("payment-cancelled")) {
            // Handle cancellation
        }
    }
}
```
