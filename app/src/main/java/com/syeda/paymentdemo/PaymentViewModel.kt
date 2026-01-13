import androidx.lifecycle.ViewModel
import com.altapay.checkout.CheckoutPaymentClient
import com.altapay.checkout.PaymentResultCheckout
import com.altapay.checkout.model.request.CreateSessionRequest
import com.altapay.checkout.model.response.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PaymentViewModel(private val paymentClient: CheckoutPaymentClient) : ViewModel() {
    private val _paymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val paymentMethod: StateFlow<PaymentMethod?> get() = _paymentMethod

    private val _availablePaymentMethods = MutableStateFlow<List<PaymentMethod>?>(null)
    val availablePaymentMethods: StateFlow<List<PaymentMethod>?> get() = _availablePaymentMethods

    private val _paymentResult = MutableStateFlow<PaymentResultCheckout?>(null)
    val paymentResult: StateFlow<PaymentResultCheckout?> get() = _paymentResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> get() = _currentSessionId

    private val _loadingState = MutableStateFlow(LoadingState.IDLE)
    val loadingState: StateFlow<LoadingState> get() = _loadingState

    enum class LoadingState {
        IDLE, AUTHENTICATING, CREATING_SESSION, FETCHING_PAYMENT_METHODS, CREATING_PAYMENT
    }

    // Step 1: Authenticate and create session
    suspend fun authenticateAndCreateSession(
        sessionRequest: CreateSessionRequest
    ) {
        _loadingState.value = LoadingState.AUTHENTICATING
        try {
            val result = paymentClient.authenticateAndCreateSession(
                sessionRequest
            )

            result.onSuccess { sessionId ->
                _currentSessionId.value = sessionId
                // Step 2: Fetch payment methods
                fetchPaymentMethods(sessionId)
            }.onFailure { throwable ->
                _error.value = "Failed to create session: ${throwable.message}"
            }
        } catch (e: Exception) {
            _error.value = "Error: ${e.message}"
        } finally {
            _loadingState.value = LoadingState.IDLE
        }
    }

    // Step 2: Fetch payment methods
    suspend fun fetchPaymentMethods(sessionId: String) {
        _loadingState.value = LoadingState.FETCHING_PAYMENT_METHODS
        try {
            val methods = paymentClient.fetchPaymentMethods(sessionId)
            if (methods != null) {
                _availablePaymentMethods.value = methods
            } else {
                _error.value = "Failed to fetch payment methods"
            }
        } catch (e: Exception) {
            _error.value = "Error fetching payment methods: ${e.message}"
        } finally {
            _loadingState.value = LoadingState.IDLE
        }
    }

    // Step 3: Client calls this when user selects a payment method
    fun selectPaymentMethod(method: PaymentMethod) {
        paymentClient.selectPaymentMethod(method)
        _paymentMethod.value = method
    }

    // Step 4: Create payment with selected method
    suspend fun createPayment() {
        val sessionId = _currentSessionId.value
        if (sessionId == null) {
            _error.value = "No session created"
            return
        }

        if (_paymentMethod.value == null) {
            _error.value = "No payment method selected"
            return
        }

        _loadingState.value = LoadingState.CREATING_PAYMENT
        try {
            val result = paymentClient.createPayment(sessionId)
            _paymentResult.value = result
        } catch (e: Exception) {
            _error.value = "Error creating payment: ${e.message}"
        } finally {
            _loadingState.value = LoadingState.IDLE
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetStates() {
        _availablePaymentMethods.value = null
        _paymentMethod.value = null
        _paymentResult.value = null
        _error.value = null
        _currentSessionId.value = null
        _loadingState.value = LoadingState.IDLE
    }
}
