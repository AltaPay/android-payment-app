# AltaPay - Android SDK (Demo Project & Library)

This repository contains a reusable **AltaPay Merchant Android SDK** along with a **demo android application** that demonstrates how to integrate and execute AltaPay payment flows using WebView.

## Merchant API flow

![Altapay-MobileApp-Setup](docs/Altapay-MobileApp-Setup.png)

## Repository Overview

This project is structured as a **multi-module Android project**:

1. **AltaPay Merchant and Checkout SDK (Library Module)**
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
├── altapayMerchant/  # Android Library module with merchant API
│   ├── src/main/java/
│   │   └── com.altapay.merchant
│   ├── build.gradle
│   └── consumer-rules.pro
│
├── altapayCheckout/  # Android Library module with Checkout API
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
├── README.md
└── wiki.md
```

## Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 21+
- Active Internet connection

## Documentation

For more details please see [docs](https://github.com/AltaPay/android-payment-app/wiki)

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.