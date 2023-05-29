package com.app.paymentapp.PaymentLibrary;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class PaymentRequestHelper {
    /*
     Declare for get application activity.
    */
    private Context context;
    private Map<String, String> map = new HashMap<>();
    private String username;
    private String password;
    private String payment_url;
    private DataCallback callback;

    // This is the constructor of this helper class
    public PaymentRequestHelper(Context context) {
        this.context = context;
    }

    /*
    Get the response of payment request API. 
    If the status of the response is Success the URL will be open on webView. 
    In case of fail or server error, it will show the error Message. 
    */
    public void CallPaymentRequestApi(final DataCallback callback) {
        this.callback = callback;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, payment_url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error + "");
            }
        }) {
            // Authentication of Payment Request.
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                header.put("Authorization", auth);
                return header;
            }

            // Get the Payment request information parameters
            @Override
            protected Map<String, String> getParams() {
                // Add Payment Request Params here
                return map;
            }
        };
        queue.add(request);
    }

    // Set the parameters of payment request API
    public void addParam(Map<String, String> params) {
        this.map = params;
    }

    // Set the valid username and password for the gateway authentication
    public void setAuthParam(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    // Set the payment request URL
    public void setPaymentUrl(String url) {
        this.payment_url = url;
    }


    //This interface is not used for outside the class.
    //this will use within payment request helper class only.
    //this is the callback function used to get onSuccess response.
    public interface DataCallback extends BaseCallback {
        void onSuccess(String data);
    }

    // Handle fail repsonse
    public interface BaseCallback {
        void onError(String msg);
    }
}
