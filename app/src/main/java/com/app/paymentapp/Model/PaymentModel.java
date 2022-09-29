package com.app.paymentapp.Model;

import java.util.ArrayList;
import java.util.List;

public class PaymentModel {

    String type;// This is the authorization type.See Payment request types:payment (default), paymentAndCapture,verifyCard, credit, subscription, subscriptionAndCharge,subscriptionAndReserve
    String payment_source;// This identifies the source of the payment. The default value is eCommerce.values are (eCommerce, mobi, moto, mail_order, telephone_order)
    String terminal;
    String shop_orderid;
    String amount;
    String currency;
    String sales_tax;//The sales tax amount. Indicates how much of the gross amount that was sales tax, e.g. VAT (UK) or Moms (DK).
    String cookie; // This is the cookie that will be sent back to all your callback URLs.
    String language;

    public PaymentModel() {
        this.type = "";
        this.payment_source = "";
        this.terminal = "";
        this.shop_orderid = "";
        this.amount = "";
        this.currency = "";
        this.sales_tax = "";
        this.cookie = "";
        this.language = "";

    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public String getPayment_source() {
        return payment_source;
    }

    public void setPayment_source(String payment_source) {
        this.payment_source = payment_source;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getShop_orderid() {
        return shop_orderid;
    }

    public void setShop_orderid(String shop_orderid) {
        this.shop_orderid = shop_orderid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSales_tax() {
        return sales_tax;
    }

    public void setSales_tax(String sales_tax) {
        this.sales_tax = sales_tax;

    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



}
