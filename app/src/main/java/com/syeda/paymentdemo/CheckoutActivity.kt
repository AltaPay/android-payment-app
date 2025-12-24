package com.syeda.paymentdemo

import PaymentViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.altapay.checkout.CheckoutPaymentClient
import com.altapay.checkout.PaymentResultCheckout
import com.altapay.checkout.model.PaymentConfig
import com.altapay.checkout.model.request.Address
import com.altapay.checkout.model.request.Amount
import com.altapay.checkout.model.request.Configuration
import com.altapay.checkout.model.request.CreateSessionRequest
import com.altapay.checkout.model.request.Customer
import com.altapay.checkout.model.request.Order
import com.altapay.checkout.model.request.OrderLine
import com.altapay.checkout.model.request.TransactionInfo
import com.altapay.checkout.model.response.PaymentMethod
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(paymentClient)
    }

    private val baseUrl = "https://testgateway.altapaysecure.com/"

    // Payment client configuration
    private val paymentConfig = PaymentConfig(
        username = "{{gateway_username}}",
        password = "{{gateway_password}}",
        baseUrl = baseUrl
    )
    private val paymentClient = CheckoutPaymentClient(paymentConfig)

    // UI Components
    private lateinit var progressBar: ProgressBar
    private lateinit var initiatePaymentButton: Button
    private lateinit var cancelButton: Button
    private lateinit var webView: WebView

    private lateinit var webviewLayout: LinearLayout
    private lateinit var controlsLayout: LinearLayout
    private lateinit var webviewProgressBar: ProgressBar
    private lateinit var closeWebViewButton: Button
    private lateinit var webviewTitle: TextView

    // Payment URLs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_activity)



        progressBar = findViewById(R.id.progressBar)
        initiatePaymentButton = findViewById(R.id.initiatingPaymentButton)
        cancelButton = findViewById(R.id.cancelButton)
        webView = findViewById(R.id.paymentWebView)
        webviewLayout = findViewById(R.id.webviewLayout)
        controlsLayout = findViewById(R.id.controlsLayout)
        webviewProgressBar = findViewById(R.id.webviewProgressBar)
        closeWebViewButton = findViewById(R.id.closeWebViewButton)
        webviewTitle = findViewById(R.id.webviewTitle)

        setupWebView()
        setupObservers()
        setupClickListeners()

        // Set initial UI state
        updateUIState(isLoading = false)
    }

    private fun setupObservers() {
        // Observe available payment methods
        lifecycleScope.launch {
            viewModel.availablePaymentMethods.collect { methods ->
                methods?.let {
                    if (it.isNotEmpty()) {
                        // Show payment method selection dialog
                        showPaymentMethodSelectionDialog(it)
                    } else {
                        showErrorDialog("No payment methods available")
                    }
                }
            }
        }

        // Observe payment result
        lifecycleScope.launch {
            viewModel.paymentResult.collect { result ->
                when (result) {
                    is PaymentResultCheckout.Success -> {
                        updateUIState(isLoading = false)
                        // Handle successful payment creation
                        val paymentUrl = result.redirectUrl
                        showPaymentSuccessDialog(paymentUrl)
                    }

                    is PaymentResultCheckout.Error -> {
                        updateUIState(isLoading = false)
                        val errorMessage = result.error.message
                        showErrorDialog("Payment error: $errorMessage")
                    }

                    null -> {
                        // No result yet, do nothing
                    }
                }
            }
        }

        // Observe loading state
        lifecycleScope.launch {
            viewModel.loadingState.collect { state ->
                when (state) {
                    PaymentViewModel.LoadingState.AUTHENTICATING ->
                        showLoading("Authenticating...")

                    PaymentViewModel.LoadingState.CREATING_SESSION ->
                        showLoading("Creating session...")

                    PaymentViewModel.LoadingState.FETCHING_PAYMENT_METHODS ->
                        showLoading("Loading payment methods...")

                    PaymentViewModel.LoadingState.CREATING_PAYMENT ->
                        showLoading("Creating payment...")

                    PaymentViewModel.LoadingState.IDLE ->
                        hideLoading()
                }
            }
        }

        // Observe errors
        lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    updateUIState(isLoading = false)
                    Toast.makeText(this@CheckoutActivity, "Error: $it", Toast.LENGTH_SHORT).show()
                    // Clear error after showing
                    viewModel.clearError()
                }
            }
        }

        // Observe selected payment method
        lifecycleScope.launch {
            viewModel.paymentMethod.collect { method ->
                method?.let {
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Selected: ${it.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        initiatePaymentButton.setOnClickListener {
            startPaymentFlow()
        }

        cancelButton.setOnClickListener {
            finishPaymentFlow()
        }
    }

    private fun startPaymentFlow() {
        // Disable button while processing
        initiatePaymentButton.isEnabled = false
        cancelButton.isVisible = true

        // Create session request
        val sessionRequest = CreateSessionRequest(
            order = Order(
                orderId = "testOrderId",
                amount = Amount(100.0, "DKK"),
                orderLines = listOf(
                    OrderLine("123981239", "Chaos Emerald", 1, 28020.5),
                    OrderLine("123981240", "Delivery", 1, 2078.02)
                ),
                customer = Customer(
                    firstName = "Demo",
                    lastName = "User",
                    email = "demo@example.com",
                    billingAddress = Address("Dantes Plads 7", "København Ø", "DK", "1556"),
                    shippingAddress = Address("Dantes Plads 7", "København Ø", "DK", "1556")
                ),
                transactionInfo = TransactionInfo("Test param1", "Test param2", "Test param3"),
            ),
            callbacks = null,
            configuration = Configuration("PAYMENT", "JSON", false, "REDIRECT", "DK", "en")
        )

        lifecycleScope.launch {
            // Step 1: Authenticate and create session
            viewModel.authenticateAndCreateSession(
                sessionRequest
            )
        }
    }

    private fun showPaymentMethodSelectionDialog(methods: List<PaymentMethod>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Payment Method")

        // Create a list of payment method names
        val methodNames = methods.mapIndexed { index, method ->
            "${method.name ?: "Unknown"} (${method.type ?: "N/A"})"
        }.toTypedArray()

        builder.setItems(methodNames) { dialog, which ->
            // User selects a payment method
            val selectedMethod = methods[which]

            // Show confirmation dialog
            showPaymentMethodConfirmation(selectedMethod) { confirmed ->
                if (confirmed) {
                    // Step 3: Set selected payment method in ViewModel
                    viewModel.selectPaymentMethod(selectedMethod)

                    // Step 4: Create payment
                    lifecycleScope.launch {
                        viewModel.createPayment()
                    }
                } else {
                    // User cancelled, show selection dialog again
                    showPaymentMethodSelectionDialog(methods)
                }
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            finishPaymentFlow()
        }

        builder.setCancelable(false)
        builder.show()
    }

    private fun showPaymentMethodConfirmation(
        method: PaymentMethod,
        onResult: (Boolean) -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment Method")
            .setMessage("Do you want to proceed with ${method.name}?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                onResult(true)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                onResult(false)
            }
            .setCancelable(false)
            .show()
    }

    private fun showPaymentSuccessDialog(paymentUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Payment Created Successfully!")
            .setMessage("Redirecting to payment page...")
            .setPositiveButton("Open Payment Page") { dialog, _ ->
                dialog.dismiss()
                // Open payment URL in WebView or browser
                openPaymentWebView(paymentUrl)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finishPaymentFlow()
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Retry") { dialog, _ ->
                dialog.dismiss()
                startPaymentFlow()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finishPaymentFlow()
            }
            .show()
    }

    private fun showLoading(message: String) {
        updateUIState(isLoading = true, message = message)
    }

    private fun hideLoading() {
        updateUIState(isLoading = false)
    }

    private fun updateUIState(isLoading: Boolean, message: String? = null) {
        progressBar.isVisible = isLoading
        initiatePaymentButton.isEnabled = !isLoading

        if (isLoading) {
            // You could show a loading dialog or update a TextView
            Toast.makeText(this, message ?: "Processing...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPaymentWebView(paymentUrl: String) {
        // Hide controls and show WebView
        controlsLayout.isVisible = false
        webviewLayout.isVisible = true

        // Load the payment URL
        webView.loadUrl(paymentUrl)

        // Optional: Show a toast
        Toast.makeText(
            this,
            "Loading payment gateway...",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun closeWebView() {
        webView.stopLoading()
        webView.loadUrl("about:blank")

        webviewLayout.isVisible = false
        controlsLayout.isVisible = true

        // Reset UI
        finishPaymentFlow()
    }

    private fun handlePaymentSuccess(url: String) {
        lifecycleScope.launch {
            AlertDialog.Builder(this@CheckoutActivity)
                .setTitle("Payment Successful!")
                .setMessage("Your payment has been processed successfully.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    closeWebView()

                    // Navigate to success screen or finish activity
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Payment completed successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    // You can finish the activity or navigate to another screen
                    // finish()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun handlePaymentFailure(url: String) {
        lifecycleScope.launch {
            AlertDialog.Builder(this@CheckoutActivity)
                .setTitle("Payment Failed")
                .setMessage("Your payment could not be processed. Please try again.")
                .setPositiveButton("Retry") { dialog, _ ->
                    dialog.dismiss()
                    closeWebView()
                    startPaymentFlow()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    closeWebView()
                }
                .show()
        }
    }

    private fun handlePaymentCancellation(url: String) {
        lifecycleScope.launch {
            AlertDialog.Builder(this@CheckoutActivity)
                .setTitle("Payment Cancelled")
                .setMessage("You have cancelled the payment.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    closeWebView()
                }
                .show()
        }
    }


    private fun finishPaymentFlow() {
        // Reset UI
        initiatePaymentButton.isEnabled = true
        cancelButton.isVisible = false

        // Clear ViewModel states
        viewModel.resetStates()

        Toast.makeText(this, "Payment flow cancelled", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        // Configure WebView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            defaultTextEncodingName = "utf-8"
        }

        // Set WebView client to handle page loading
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webviewProgressBar.isVisible = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webviewProgressBar.isVisible = false
                webviewTitle.text = view?.title ?: "Payment Gateway"
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                webviewProgressBar.isVisible = false
                Toast.makeText(
                    this@CheckoutActivity,
                    "WebView error: $description",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    // Check if this is a payment completion URL
                    if (it.contains("payment-success") || it.contains("callback/success")) {
                        handlePaymentSuccess(url)
                        return true
                    } else if (it.contains("payment-failed") || it.contains("callback/failure")) {
                        handlePaymentFailure(url)
                        return true
                    } else if (it.contains("payment-cancelled") || it.contains("callback/cancel")) {
                        handlePaymentCancellation(url)
                        return true
                    }
                }
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                webviewProgressBar.progress = newProgress
            }
        }

        // Handle close button click
        closeWebViewButton.setOnClickListener {
            closeWebView()
        }
    }
}