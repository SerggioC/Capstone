package com.sergiocruz.capstone.util;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.sergiocruz.capstone.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static String getDate(long dateMillis) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        Date resultDate = new Date(dateMillis);
        return dateFormatter.format(resultDate);

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public static AlertDialog.Builder buildNoNetworkDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.no_internet);
        builder.setPositiveButton(android.R.string.ok, Utils::onClick);
        return builder;
    }

    private static void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}