import okhttp3.Interceptor
import okhttp3.Response

class ContentTypeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        val requestBuilder = originalRequest.newBuilder()

        // Add Content-Type for JSON endpoints
        if (originalRequest.method == "POST" &&
            (url.contains("/session") || url.contains("/payment"))) {

            // Check if Content-Type is already set
            val contentType = originalRequest.header("Content-Type")
            if (contentType == null) {
                requestBuilder.addHeader("Content-Type", "application/json")
                println("ContentTypeInterceptor - Added Content-Type: application/json")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}