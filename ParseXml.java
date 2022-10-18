package com.app.paymentapp.PaymentLibrary;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.gson.Gson;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;

public class ParseXml {
    // Convert XML to Json
    public String parseData(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            DataModel model = new DataModel();
            xpp.setInput(new StringReader(response)); 
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG)
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
                    // Get the xml in a string format
                    Log.e("TagText", xpp.getText()); 
                    // Get the Url from the response
                    if (model.isAdded)
                    {
                        if (model.text == null) {
                            model.text = xpp.getText();
                        }

                    }
                }
                eventType = xpp.next();
            }
            Log.e("modelDataEnd: ", new Gson().toJson(model) + "");
            return model.text;

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private class DataModel {
        public String name, text;
        public boolean isAdded;
    }
}
