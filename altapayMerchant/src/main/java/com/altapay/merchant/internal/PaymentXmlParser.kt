package com.altapay.merchant.internal

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

internal object PaymentXmlParser {

    fun extractRedirectUrl(xml: String): String? {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(StringReader(xml))

        var event = parser.eventType
        var insideUrlTag = false

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> if (parser.name.equals("Url", ignoreCase = true)) {
                    insideUrlTag = true
                }
                XmlPullParser.TEXT -> if (insideUrlTag) {
                    val value = parser.text?.trim()
                    if (!value.isNullOrBlank()) return value
                }
                XmlPullParser.END_TAG -> if (parser.name.equals("Url", ignoreCase = true)) {
                    insideUrlTag = false
                }
            }
            event = parser.next()
        }
        return null
    }
}
