package com.app.paymentapp;

import android.app.ProgressDialog;
import android.content.Context;


public class Utils {
    public static ProgressDialog progressDialog;

    public static void showDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public static void dismiss() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}