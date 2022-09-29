package com.app.paymentapp.Model;

//This is a one-dimensional associative array, where you can put any value that you
// would like to associate with the payment in the call to createPaymentRequest.

public class TransactionInfoModel {
    String ecomPlatform;
    String ecomVersion;
    String ecomPluginName;
    String ecomPluginVersion;
    String otherInfo;

    public TransactionInfoModel() {
        this.ecomPlatform = "";
        this.ecomVersion = "";
        this.ecomPluginName = "";
        this.ecomPluginVersion = "";
        this.otherInfo = "";
    }

    public String getEcomPlatform() {
        return ecomPlatform;
    }

    public void setEcomPlatform(String ecomPlatform) {
        this.ecomPlatform = ecomPlatform;
    }

    public String getEcomVersion() {
        return ecomVersion;
    }

    public void setEcomVersion(String ecomVersion) {
        this.ecomVersion = ecomVersion;
    }

    public String getEcomPluginName() {
        return ecomPluginName;
    }

    public void setEcomPluginName(String ecomPluginName) {
        this.ecomPluginName = ecomPluginName;
    }

    public String getEcomPluginVersion() {
        return ecomPluginVersion;
    }

    public void setEcomPluginVersion(String ecomPluginVersion) {
        this.ecomPluginVersion = ecomPluginVersion;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
}
