package com.app.paymentapp;

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
    private WebView webView;

    public PaymentRequestHelper(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
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
                parseXml(response);
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
                //Add Payment Request Params here
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


    // Convert XML to Json
    private void parseXml(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            DataModel model = new DataModel();
            xpp.setInput(new StringReader(response)); // pass input whatever xml you have
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG)// here you get the Start Tag
                {
                    if (xpp.getName().equals("Url")) {
                        if (!model.isAdded) {
                            model.name = xpp.getName();
                            model.isAdded = true;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.e("Tag End ", xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.e("TagText", xpp.getText()); // here you get the text from xml

                    if (model.isAdded)//this condition is applied to get url from the response.
                    {
                        if (model.text == null) {
                            model.text = xpp.getText();
                        }
                        /*
                        Add data in model or any variable and set it in callback.onSuccess
                        function which is call in MyJavaScriptInterface class.
                        */
                     //  if(condition){
                     //  }
                    }

                }
                eventType = xpp.next();
            }
            Log.e("modelDataEnd: ", new Gson().toJson(model) + "");

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(mWebViewClient);
            webView.addJavascriptInterface(new MyJavaScriptInterface(),
                    "android");
            webView.loadUrl(model.text);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            // if you want to close the webView, write the logic here
            callback.onSuccess("Add value here");
        }
    }

    private class DataModel {
        public String name, text;
        public boolean isAdded;
    }

    // Handle the success response
    public interface DataCallback extends BaseCallback {
        void onSuccess(String data);
    }

    // Handle fail repsonse
    public interface BaseCallback {
        void onError(String msg);
    }
}
