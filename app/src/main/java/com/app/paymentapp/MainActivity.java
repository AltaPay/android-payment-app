package com.app.paymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.paymentapp.Model.ConfigModel;
import com.app.paymentapp.Model.CustomerInfo;
import com.app.paymentapp.Model.DataModel;
import com.app.paymentapp.Model.OrderLinesModel;
import com.app.paymentapp.Model.PaymentModel;
import com.app.paymentapp.Model.TransactionInfoModel;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    Context context;
    PaymentModel paymentModel = new PaymentModel();
    CustomerInfo customerInfo = new CustomerInfo();
    ConfigModel configModel = new ConfigModel();
    TransactionInfoModel infoModel = new TransactionInfoModel();
    OrderLinesModel orderLinesModel = new OrderLinesModel();
    OrderLinesModel orderLinesModel1 = new OrderLinesModel();
    List<OrderLinesModel> orderLines = new ArrayList<>();
    TextView btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        webView = findViewById(R.id.webView);
        btnPayment = findViewById(R.id.btnPayment);

        SetDataModel();

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showDialog(context);
                String username = "ij@technologies.dk";
                String password = "WQJ&le3cxb7g@wH4Nh";
                paymentRequestMethod(
                        "https://testgateway.pensio.com/merchant/API/createPaymentRequest",
                        username,
                        password,
                        paymentModel,
                        customerInfo,
                        configModel,
                        infoModel,
                        orderLines
                );
            }
        });

    }

    private void SetDataModel() {

        paymentModel.setType("payment");
        paymentModel.setPayment_source("eCommerce");
        paymentModel.setTerminal("EmbraceIT Test Terminal");
        paymentModel.setShop_orderid("26");
        paymentModel.setAmount("100");
        paymentModel.setCurrency("DKK");
        paymentModel.setLanguage("en");

        customerInfo.setEmail("test@yahoo.com");

        customerInfo.setClient_ip("203.135.21.10");
        customerInfo.setClient_user_agent("");
        customerInfo.setClient_accept_language("");
        customerInfo.setClient_session_id("");
        customerInfo.setCustomer_phone("");
        customerInfo.setBilling_firstname("test");
        customerInfo.setBilling_lastname("test 2");
        customerInfo.setBilling_address("test");
        customerInfo.setBilling_city("test");
        customerInfo.setBilling_postal("1234");
        customerInfo.setBilling_region("");
        customerInfo.setBilling_country("DK");

        customerInfo.setShipping_firstname("test");
        customerInfo.setShipping_lastname("test 2");
        customerInfo.setShipping_address("test");
        customerInfo.setShipping_city("test");
        customerInfo.setShipping_postal("1234");
        customerInfo.setShipping_country("DK");
        customerInfo.setShipping_region("");

        orderLinesModel.setDescription("Total");
        orderLinesModel.setItemId("order-total");
        orderLinesModel.setQuantity("1");
        orderLinesModel.setTaxAmount("");
        orderLinesModel.setUnitPrice("100");
        orderLinesModel.setUnitCode("");
        orderLinesModel.setTaxPercent("");
        orderLinesModel.setDiscount("");
        orderLinesModel.setGoodsType("");
        orderLinesModel.setImageUrl("");
        orderLinesModel.setProductUrl("");
        orderLines.add(orderLinesModel);

        orderLinesModel1.setDescription("flatrate");
        orderLinesModel1.setItemId("flatrate");
        orderLinesModel1.setQuantity("1");
        orderLinesModel1.setUnitPrice("5.0000");
        orderLinesModel1.setTaxPercent("0");
        orderLinesModel1.setTaxAmount("0.0000");
        orderLinesModel1.setDiscount("0");
        orderLinesModel1.setGoodsType("shipment");
        orderLines.add(orderLinesModel1);

        configModel.setCallback_form("http://34.243.11.73/wordpress/altapay-payment-form/");
        configModel.setCallback_ok("http://34.243.11.73/wordpress/checkout/order-received/26/?key=wc_order_ZAVmJMGdpyIEB&type=ok&wc-api=WC_Gateway_altapay_embraceit_test_terminal");
        configModel.setCallback_fail("http://34.243.11.73/wordpress/checkout/order-received/26/?key=wc_order_ZAVmJMGdpyIEB&type=fail&wc-api=WC_Gateway_altapay_embraceit_test_terminal");
        configModel.setCallback_open("http://34.243.11.73/wordpress/checkout/order-received/26/?key=wc_order_ZAVmJMGdpyIEB&type=open&wc-api=WC_Gateway_altapay_embraceit_test_terminal");
        configModel.setCallback_notification("http://34.243.11.73/wordpress/checkout/order-received/26/?key=wc_order_ZAVmJMGdpyIEB&type=notification&wc-api=WC_Gateway_altapay_embraceit_test_terminal");
        configModel.setCallback_redirect("");

        infoModel.setOtherInfo("Demo Shop");
        infoModel.setEcomPlatform("Magento");
        infoModel.setEcomVersion("2.4.1");
        infoModel.setEcomPluginName("SDM_Altapay");
        infoModel.setEcomPluginVersion("3.3.7");
        paymentModel.setSales_tax("0");
        paymentModel.setCookie(getString(R.string.cookie));

        Log.e("SetDataModel:", new Gson().toJson(paymentModel) + "");

    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void paymentRequestMethod(String url, String username, String password,
                                      PaymentModel paymentModel, CustomerInfo customerInfo,
                                      ConfigModel configModel, TransactionInfoModel infoModel,
                                      List<OrderLinesModel> orderLines) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.dismiss();
                parseXml(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismiss();
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                header.put("Authorization", auth);
                return header;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("type", paymentModel.getType());
                map.put("payment_source", paymentModel.getPayment_source());
                map.put("terminal", paymentModel.getTerminal());
                map.put("shop_orderid", paymentModel.getShop_orderid());
                map.put("amount", paymentModel.getAmount());
                map.put("currency", paymentModel.getCurrency());

                map.put("customer_info[email]", customerInfo.getEmail());
                map.put("customer_info[customer_phone]", customerInfo.getCustomer_phone());
                map.put("customer_info[client_ip]", customerInfo.getClient_ip());
                map.put("customer_info[client_session_id]", customerInfo.getClient_session_id());
                map.put("customer_info[client_accept_language]", customerInfo.getClient_accept_language());
                map.put("customer_info[client_user_agent]", customerInfo.getClient_user_agent());
                map.put("customer_info[billing_firstname]", customerInfo.getBilling_firstname());
                map.put("customer_info[billing_lastname]", customerInfo.getBilling_lastname());
                map.put("customer_info[billing_address]", customerInfo.getBilling_address());
                map.put("customer_info[billing_city]", customerInfo.getBilling_city());
                map.put("customer_info[billing_region]", customerInfo.getBilling_region());
                map.put("customer_info[billing_postal]", customerInfo.getBilling_postal());
                map.put("customer_info[billing_country]", customerInfo.getBilling_country());

                map.put("customer_info[shipping_firstname]", customerInfo.getShipping_firstname());
                map.put("customer_info[shipping_lastname]", customerInfo.getShipping_lastname());
                map.put("customer_info[shipping_address]", customerInfo.getShipping_address());
                map.put("customer_info[shipping_city]", customerInfo.getShipping_city());
                map.put("customer_info[shipping_region]", customerInfo.getShipping_region());
                map.put("customer_info[shipping_postal]", customerInfo.getShipping_postal());
                map.put("customer_info[shipping_country]", customerInfo.getShipping_country());

                map.put("config[callback_form]", configModel.getCallback_form());
                map.put("config[callback_fail]",configModel.getCallback_fail());
                map.put("config[callback_open]", configModel.getCallback_open());
                map.put("config[callback_notification]", configModel.getCallback_notification());
                map.put("config[callback_ok]", configModel.getCallback_ok());
                map.put("config[callback_redirect]", configModel.getCallback_redirect());

                map.put("sales_tax",paymentModel.getSales_tax());
                map.put("cookie", paymentModel.getCookie());

                map.put("transaction_info[otherInfo]", infoModel.getOtherInfo());
                map.put("transaction_info[ecomPlatform]", infoModel.getEcomPlatform());
                map.put("transaction_info[ecomVersion]", infoModel.getEcomVersion());
                map.put("transaction_info[ecomPluginName]", infoModel.getEcomPluginName());
                map.put("transaction_info[ecomPluginVersion]", infoModel.getEcomPluginVersion());

                for (int i = 0; i < orderLines.size(); i++) {
                    map.put("orderLines[" + i + "][description]", orderLines.get(i).getDescription());
                    map.put("orderLines[" + i + "][itemId]", orderLines.get(i).getItemId());
                    map.put("orderLines[" + i + "][quantity]", orderLines.get(i).getQuantity());
                    map.put("orderLines[" + i + "][unitPrice]", orderLines.get(i).getUnitPrice());
                    map.put("orderLines[" + i + "][taxPercent]", orderLines.get(i).getTaxPercent());
                    map.put("orderLines[" + i + "][taxAmount]", orderLines.get(i).getTaxAmount());
                    map.put("orderLines[" + i + "][unitCode]", orderLines.get(i).getUnitCode());
                    map.put("orderLines[" + i + "][discount]", orderLines.get(i).getDiscount());
                    map.put("orderLines[" + i + "][goodsType]", orderLines.get(i).getGoodsType());
                    map.put("orderLines[" + i + "][imageUrl]",orderLines.get(i).getImageUrl());
                    map.put("orderLines[" + i + "][productUrl]", orderLines.get(i).getProductUrl());
                }
                map.put("language", paymentModel.getLanguage());
                return map;
            }
        };
        queue.add(request);
    }

    public void parseXml(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            DataModel model = new DataModel();
            xpp.setInput(new StringReader(response)); // pass input whatever xml you have
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
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
                    if (model.isAdded) {
                        if (model.text == null) {
                            model.text = xpp.getText();
                        }
                    }

                }
                eventType = xpp.next();
            }
            Log.e("modelDataEnd: ", new Gson().toJson(model) + "");
            getURL(model.text);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getURL(String url) {
        webView.setVisibility(View.VISIBLE);
        btnPayment.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewController());
    }
}
