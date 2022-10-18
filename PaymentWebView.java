package com.app.paymentapp.PaymentLibrary;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PaymentWebView {
    URLChangedCallback callback;
    WebView webView;

    // Set the content/Url on the webView.
    public void setPaymentWebView(WebView webView, String URL, final URLChangedCallback callback) {
        this.callback = callback;
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(mWebViewClient);
        webView.addJavascriptInterface(new MyJavaScriptInterface(),
                "android");
        webView.loadUrl(URL);

    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
        }
    };

    // This method trigger every time whenever the URL change.
    class MyJavaScriptInterface {
        @JavascriptInterface
        public void onUrlChange(String url) {
            Log.e("hydrated", "onUrlChange" + url);
            // Close webView or perform any action 
            callback.onAction(url);
        }
    }

    // Trigger when url changed
    public interface URLChangedCallback {
        void onAction(String data);
    }

}
