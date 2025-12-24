package com.altapay.checkout


import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

object PaymentWebViewHelper {

    /**
     * Observes navigations; host app decides what URLs mean "success" or "cancel".
     */
    fun attach(
        webView: WebView,
        onUrlChanged: (String) -> Unit
    ) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if (!url.isNullOrBlank()) onUrlChanged(url)
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (!url.isNullOrBlank()) onUrlChanged(url)
            }
        }
    }
}
