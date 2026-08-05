# AltaPay Android SDK – Integration Wiki

This document explains how to integrate AltaPay payments into an Android application using the provided SDKs.  
It covers **project setup**, **demo app usage**, and **two integration approaches**:

- **Merchant API(`altapayMerchant`)**
- **Checkout API (`altapayCheckout`)**
- **[Handling Payment Callbacks](#handling-payment-callbacks)**: Web vs. Native app-to-app redirect flow

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
| callback_ok       | Notifies the merchant the payment succeeded. See [Handling Payment Callbacks](#handling-payment-callbacks). |
| callback_failure  | Notifies the merchant the payment failed. See [Handling Payment Callbacks](#handling-payment-callbacks). |
| callback_redirect | Styling for the loading page shown while the customer is redirected to a third party (e.g. 3-D Secure). **Not** a final redirect target (see below). |

## Handling Payment Callbacks

### CallbackRedirect (`redirect`)

[`callbacks.redirect`](https://documentation.altapay.com/v2/Checkout-API/Integration/#configuring-payment-status-callbacks) tells Checkout where to send the customer back to once they're done interacting with the payment method (e.g. Bancontact). It can be an `HTTPS` URL or a custom app protocol, and App Links (Android) / Universal Links (iOS) are recommended over a bare custom scheme.

Which flow applies is controlled by `isNativeFlow`, a boolean root-level session parameter on `CreateSessionRequest`, not by what `redirect` is set to (`redirect` itself is used differently in each flow, described below):

```kotlin
val sessionRequest = CreateSessionRequest(
    order = order,
    callbacks = callbacks,
    configuration = configuration,
    isNativeFlow = true
)
```

- **Web-Based Flow (`isNativeFlow = false` or unset):** the payment page renders in a WebView. Once the payment method finishes, the Gateway calls `callback_ok`/`callback_failure` server-to-server, then the Gateway navigates the WebView to `redirect`. On Android, intercept this via `PaymentWebViewHelper.attach`'s `onUrlChanged` and close the WebView:

  ```kotlin
  val redirectUrl = "https://merchant.example.com/app-link"

  PaymentWebViewHelper.attach(webView) { url ->
      if (url.startsWith(redirectUrl)) {
          closeWebView()
      }
  }
  ```

- **Native App Flow (`isNativeFlow = true`):** Checkout skips the payment page entirely for app-based payment methods and redirects the customer straight into the payment method's app, using `AppUrl` (a field returned in the Merchant API's `createPaymentRequest` response that, when POSTed to with device info, returns a native redirect URL, e.g. `mobilepayonline-test://...`), instead of passing `CallbackRedirect` through to the Merchant API's `callback_redirect`. The payment method's app then redirects back via `redirect` as an OS-level deep link, which requires an `<intent-filter>` registered in `AndroidManifest.xml`:

  ```xml
  <activity android:name=".CheckoutActivity" android:exported="true">
      <intent-filter>
          <action android:name="android.intent.action.VIEW" />
          <category android:name="android.intent.category.DEFAULT" />
          <category android:name="android.intent.category.BROWSABLE" />
          <data android:scheme="https" android:host="merchant.example.com" android:pathPrefix="/app-link" />
      </intent-filter>
  </activity>
  ```

  The [official `AppUrl` docs](https://documentation.altapay.com/Content/Ecom/Scenarios/Initiate%20credit%20card%20wallet%20payment%20in%20APP.htm) list MobilePay and Vipps; Bancontact support has since been added but isn't reflected on that page yet.

> When integrating directly against the Merchant API, do not pass an app deep link as [`callback_redirect`](https://documentation.altapay.com/Content/Ecom/Payment%20Pages/Payment%20Page%20Redirect.htm): that page is shown while the customer is being redirected to a third party (e.g. 3-D Secure) and "should not do anything except tell the customer that they are being redirected." Forms and meta tags are stripped from it, so it can't even serve as a redirect target itself.

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
Monitor the WebView’s state to detect when the payment flow has finished, so you know when to close the WebView.

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

> **Do not treat these URL keywords as the source of truth for the payment result.** As covered in [Handling Payment Callbacks](#handling-payment-callbacks), the actual success/failure result is delivered to your backend via `CallbackSuccess`/`CallbackFailure`, not through the WebView's navigation. Use URL matching only to decide when to close the WebView (see [Handling the App Return URL on Android](#handling-the-app-return-url-on-android)), and confirm the outcome by querying your backend's order status.
