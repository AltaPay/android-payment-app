package com.syeda.paymentdemo
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/*class MainActivity : AppCompatActivity() {
    lateinit var progressDialog:ProgressDialog
    lateinit var webView: WebView
    lateinit var btnPayment: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Creating Payment Request")
        progressDialog.setMessage("Loading")
        webView = findViewById(R.id.webView)
        btnPayment = findViewById(R.id.btnPayment)
        btnPayment.setOnClickListener {
            createPaymentRequest()
        }


    }

    private fun createPaymentRequest() {
        progressDialog.show()
        val helper = PaymentRequestHelper(this@MainActivity);
        helper.setPaymentUrl("https://testgateway.pensio.com/merchant/API/createPaymentRequest");
        helper.addParam(getParam());
        helper.setAuthParam("{gateway_username}", "{gateway_password}");
        helper.CallPaymentRequestApi(object : PaymentRequestHelper.DataCallback {
            override fun onError(msg: String?) {
                Log.e("onError", "->" + msg)
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity,""+msg,Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(data: String?) {
                Log.e("onSuccess", "->" + data)
                val paymentWebView = PaymentWebView();
                progressDialog.dismiss()
                paymentWebView.setPaymentWebView(webView,
                    extractUrl(data)
                ) { }
            }

        })
    }

    private fun getParam(): MutableMap<String, String> {
        val map: HashMap<String, String> = HashMap();
        map["terminal"] = "EmbraceIT Test Terminal"
        map["shop_orderid"] = "android100"
        map["amount"] = "100"
        map["currency"] = "DKK"
        map["config[callback_form]"] = "https://6901f4371c0b.ngrok-free.app/examples/exm1.php"
        return map
    }

    fun extractUrl(response: String?): String {
        val regex = "<Url>(.*?)</Url>".toRegex()
        val matchResult = response?.let { regex.find(it) }
        return matchResult?.groups?.get(1)?.value.toString()
    }
}*/


import com.altapay.merchant.*
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var btnPayment: Button
    private lateinit var progressDialog: ProgressDialog

    private lateinit var paymentClient: PaymentClient
    private var paymentJob: Job? = null   // for cancellation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Creating Payment Request")
            setMessage("Loading")
            setCancelable(false)
        }

        webView = findViewById(R.id.webView)
        btnPayment = findViewById(R.id.btnPayment)

        // Initialize SDK client ONCE
        paymentClient = PaymentClient(
            PaymentConfig(
                username = "{{gateway_username}}",
                password = "{{gateway_password}}",
                enableHttpLogging = true // false in production
            )
        )

        btnPayment.setOnClickListener {
            createPaymentRequest()
        }
    }

    private fun createPaymentRequest() {
        progressDialog.show()
        btnPayment.isEnabled = false

        // Call payment API
        paymentJob = paymentClient.createPaymentAsync(
            paymentUrl = "https://testgateway.altapaysecure.com/merchant/API/createPaymentRequest",
            params = getParam(),
            callback = object : PaymentCallback {

                override fun onSuccess(result: PaymentResult) {

                    if (!isFinishing) progressDialog.dismiss()
                    Log.d("Payment", "Redirect URL: ${result.redirectUrl}")

                    btnPayment.isEnabled = true

                    // Attach WebView helper
                    PaymentWebViewHelper.attach(webView) { url ->
                        Log.d("WebView", "URL changed: $url")
                        // Detect success / cancel URLs here
                    }

                    webView.loadUrl(result.redirectUrl)
                }

                override fun onError(error: PaymentError) {
                    if (!isFinishing) progressDialog.dismiss()
                    btnPayment.isEnabled = true
                    Toast.makeText(
                        this@MainActivity,
                        error.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun getParam(): Map<String, String> =
        mapOf(
            "terminal" to "AltaPay Test Terminal",
            "shop_orderid" to "android100",
            "amount" to "100",
            "currency" to "DKK",
        )

    override fun onDestroy() {
        super.onDestroy()
        // Cancel request if screen closes
        paymentJob?.cancel()
    }
}
