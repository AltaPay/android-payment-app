package com.app.paymentapp.Model;

public class CustomerInfo {
    String email;//       The customer's email address.
    String customer_phone;// The customer's telephone number, without spaces.This must include the country code. You can prefix the code with+ (e.g. +446721846), or 00 (e.g. 00446721846), or omit the prefix (e.g. 446721846).
    String client_ip;
    String client_session_id;// A unique identifier of the customers session(eg. an md5 hash of the real session id). Used for fraud detection.
    String client_accept_language;//       The language setting of the customers browser. Used for fraud detection.
    String client_user_agent;//        The customers browser identification. Used for fraud detection.
    String billing_firstname;//        The first name for the customer's billing address.
    String billing_lastname;//       The last name for the customer's billing address.Mandatory if your MCC code is 6012.
    String billing_address;//        The first name for the customer's billing address.
    String billing_city;//        The city of the customer's billing address.Mandatory for fraud detection.
    String billing_region;//        The region of the customer's billing address.Mandatory for fraud detection.
    String billing_postal;//      The postal code of the customer's billing address.Mandatory if your MCC code is 6012.Mandatory for fraud detection.
    String billing_country;//        The country of the customer's billing address as a 2 character ISO-3166 country code.Mandatory for fraud detection
    String shipping_firstname;//      The first name for the customer's shipping address.
    String shipping_lastname;//       The last name for the customer's shipping address.
    String shipping_address;//     	The street address of the customer's shipping address.
    String shipping_city;//      The city of the customer's shipping address.
    String shipping_region;//        The region of the customer's shipping address.
    String shipping_postal;//      The postal code of the customer's shipping address.
    String shipping_country;//      The country of the customer's shipping address as a 2 character ISO-3166 country code.

    public CustomerInfo() {
        this.email = "";
        this.customer_phone = "";
        this.client_ip = "";
        this.client_session_id = "";
        this.client_accept_language = "";
        this.client_user_agent = "";
        this.billing_firstname = "";
        this.billing_lastname = "";
        this.billing_address = "";
        this.billing_city = "";
        this.billing_region = "";
        this.billing_postal = "";
        this.billing_country = "";
        this.shipping_firstname = "";
        this.shipping_lastname = "";
        this.shipping_address = "";
        this.shipping_city = "";
        this.shipping_region = "";
        this.shipping_postal = "";
        this.shipping_country = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getClient_session_id() {
        return client_session_id;
    }

    public void setClient_session_id(String client_session_id) {
        this.client_session_id = client_session_id;
    }

    public String getClient_accept_language() {
        return client_accept_language;
    }

    public void setClient_accept_language(String client_accept_language) {
        this.client_accept_language = client_accept_language;
    }

    public String getClient_user_agent() {
        return client_user_agent;
    }

    public void setClient_user_agent(String client_user_agent) {
        this.client_user_agent = client_user_agent;
    }

    public String getBilling_firstname() {
        return billing_firstname;
    }

    public void setBilling_firstname(String billing_firstname) {
        this.billing_firstname = billing_firstname;
    }

    public String getBilling_lastname() {
        return billing_lastname;
    }

    public void setBilling_lastname(String billing_lastname) {
        this.billing_lastname = billing_lastname;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getBilling_city() {
        return billing_city;
    }

    public void setBilling_city(String billing_city) {
        this.billing_city = billing_city;
    }

    public String getBilling_region() {
        return billing_region;
    }

    public void setBilling_region(String billing_region) {
        this.billing_region = billing_region;
    }

    public String getBilling_postal() {
        return billing_postal;
    }

    public void setBilling_postal(String billing_postal) {
        this.billing_postal = billing_postal;
    }

    public String getBilling_country() {
        return billing_country;
    }

    public void setBilling_country(String billing_country) {
        this.billing_country = billing_country;
    }

    public String getShipping_firstname() {
        return shipping_firstname;
    }

    public void setShipping_firstname(String shipping_firstname) {
        this.shipping_firstname = shipping_firstname;
    }

    public String getShipping_lastname() {
        return shipping_lastname;
    }

    public void setShipping_lastname(String shipping_lastname) {
        this.shipping_lastname = shipping_lastname;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getShipping_city() {
        return shipping_city;
    }

    public void setShipping_city(String shipping_city) {
        this.shipping_city = shipping_city;
    }

    public String getShipping_region() {
        return shipping_region;
    }

    public void setShipping_region(String shipping_region) {
        this.shipping_region = shipping_region;
    }

    public String getShipping_postal() {
        return shipping_postal;
    }

    public void setShipping_postal(String shipping_postal) {
        this.shipping_postal = shipping_postal;
    }

    public String getShipping_country() {
        return shipping_country;
    }

    public void setShipping_country(String shipping_country) {
        this.shipping_country = shipping_country;
    }
}
