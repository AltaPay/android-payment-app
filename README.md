# AltaPay - Android SDK (Demo Project & Library)

This repository contains a reusable **AltaPay Merchant Android SDK** along with a **demo android application** that demonstrates how to integrate and execute AltaPay payment flows using WebView.

## Merchant API flow

![Altapay-MobileApp-Setup](docs/Altapay-MobileApp-Setup.png)

## Repository Overview

This project is structured as a **multi-module Android project**:

1. **AltaPay Merchant SDK (Library Module)**
   - Clean, reusable SDK for creating AltaPay payment requests
   - Retrofit + Kotlin + Coroutines based
   - No Android `Context` dependency

2. **Demo Android Application**
   - Shows real-world SDK usage
   - WebView-based payment flow
   - Progress dialog handling

## Repository Structure

```
android-payment-app/
│
├── altapayMerchant/        # Android library module (SDK)
│   ├── src/main/java/
│   │   └── com.altapay.merchant
│   ├── build.gradle
│   └── consumer-rules.pro
│
├── app/                     # Demo Android application
│   ├── src/main/java/
│   │   └── com.syeda.paymentdemo
│   ├── src/main/res/layout/
│   └── build.gradle
│
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
```

## Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 21+
- Active Internet connection

## How to use

- Clone the repository
    ```bash
    git clone https://github.com/AltaPay/android-payment-app.git
    cd android-payment-app
    ```

- Open the project in **Android Studio**:

    ```
    File > Open > select android-payment-app
    ```

    Wait for Gradle sync to complete.

    ### How to Run the Demo App

    1. Select the **app** run configuration
    2. Connect an Android device or start an emulator
    3. Click **Run**

    ### Demo App Configuration

    Update AltaPay credentials in `MainActivity.kt`:

    ```kotlin
    val paymentClient = PaymentClient(
        PaymentConfig(
            username = "YOUR_API_USERNAME",
            password = "YOUR_API_PASSWORD"
        )
    )
    ```
    > Do not hardcode credentials in production apps.

## Integrating SDK into your app

### 1. Add SDK Module

1. Copy `altapayMerchant` into your project root.
2. Update `settings.gradle`:

    ```gradle
    include(":altapayMerchant")
    ```

3. Add dependency in `app/build.gradle`: and Sync Gradle.

    ```gradle
    dependencies {
        implementation project(":altapayMerchant")
    }
    ```



### 2. Required Permissions

Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Initialize Payment Client

```kotlin
val paymentClient = PaymentClient(
    PaymentConfig(
        username = "YOUR_API_USERNAME",
        password = "YOUR_API_PASSWORD"
    )
)
```

### 4. Prepare Payment Parameters

```kotlin
fun getPaymentParams(): Map<String, String> = mapOf(
    "terminal" to "YourTerminal",
    "shop_orderid" to "ORDER_12345",
    "amount" to "100.00",
    "currency" to "EUR",
    "config[callback_form]" to "https://yourdomain.com/callback"
)
```

### 5. Execute Payment Request (Callback API)

```kotlin
paymentClient.createPaymentAsync(
    paymentUrl = "https://gateway.altapay.com/payment",
    params = getPaymentParams(),
    callback = object : PaymentCallback {

        override fun onSuccess(result: PaymentResult) {
            webView.loadUrl(result.redirectUrl)
        }

        override fun onError(error: PaymentError) {
            Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
        }
    }
)
```

### 6. Coroutine-Based Usage (Optional)

```kotlin
lifecycleScope.launch {
    try {
        val result = paymentClient.createPayment(
            paymentUrl = "https://gateway.altapay.com/payment",
            params = getPaymentParams()
        )
        webView.loadUrl(result.redirectUrl)
    } catch (e: PaymentException) {
        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
    }
}
```

### 7. WebView Configuration

```kotlin
webView.settings.javaScriptEnabled = true
webView.settings.domStorageEnabled = true
webView.webViewClient = WebViewClient()
```

## Payment Request Parameters

| Parameter | Description |
|---------|-------------|
| terminal | Merchant terminal identifier |
| shop_orderid | Unique order ID |
| amount | Payment amount |
| currency | Currency code |
| Other parameters | Parameters value |

## Error Handling

- Network errors
- Authentication failures
- Invalid XML responses
- Parsing exceptions

Errors are returned via `PaymentError` or thrown as `PaymentException`.

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.

