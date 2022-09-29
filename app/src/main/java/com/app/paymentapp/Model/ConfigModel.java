package com.app.paymentapp.Model;

public class ConfigModel {

    String callback_form;//	This is the callback form, or the payment page, and corresponds to the Callback url (form) setting in the Terminal settings. For more information, see Settings for the Payment Page (callback_form). For information about custom styling for the payment page, see Styling the payment page (callback_form).
    String callback_ok;//This url is called when a payment succeeds, and corresponds to the Callback url (Ok) setting in Terminal settings. For more information, see Settings for the Payment OK page (callback_ok).
    String callback_fail;//	This url is called when a payment fails, and corresponds to the Callback url (Fail) setting in Terminal settings. For more information, see Settings for the fail page (callback_fail).
    String callback_redirect;//This url is called whenever the customer is redirected to a third party, and corresponds to the Callback url (redirect) setting in Terminal settings. For more information, see Settings for the redirect page (callback_redirect).
    String callback_open;//This url is called when a payment returns with status Open, and corresponds to the Callback url (open) setting in Terminal settings. For more information, see Settings for the open page (callback_open).
    String callback_notification;//This url is called when a notification is returned after the customer has left the payment flow, and corresponds to the Callback url (notification) setting in Terminal settings. For more information, see Payment Notification (callback_notification)

    public ConfigModel() {
        this.callback_form = "";
        this.callback_ok = "";
        this.callback_fail = "";
        this.callback_redirect = "";
        this.callback_open = "";
        this.callback_notification = "";
    }

    public String getCallback_form() {
        return callback_form;
    }

    public void setCallback_form(String callback_form) {
        this.callback_form = callback_form;
    }

    public String getCallback_ok() {
        return callback_ok;
    }

    public void setCallback_ok(String callback_ok) {
        this.callback_ok = callback_ok;
    }

    public String getCallback_fail() {
        return callback_fail;
    }

    public void setCallback_fail(String callback_fail) {
        this.callback_fail = callback_fail;
    }

    public String getCallback_redirect() {
        return callback_redirect;
    }

    public void setCallback_redirect(String callback_redirect) {
        this.callback_redirect = callback_redirect;
    }

    public String getCallback_open() {
        return callback_open;
    }

    public void setCallback_open(String callback_open) {
        this.callback_open = callback_open;
    }

    public String getCallback_notification() {
        return callback_notification;
    }

    public void setCallback_notification(String callback_notification) {
        this.callback_notification = callback_notification;
    }

}
