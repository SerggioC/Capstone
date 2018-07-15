package com.sergiocruz.capstone.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;

import com.sergiocruz.capstone.R;

import timber.log.Timber;

public class Utils {

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
        builder.setPositiveButton(android.R.string.ok, Utils::onClickOK);
        return builder;
    }

    private static void onClickOK(DialogInterface dialog, int which) {
        dialog.dismiss();
    }


    public static int dpToPx(float dpValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static void moveUpAnimation(View view, Context context) {
        view.setTag(R.id.touched, true);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.overlay), "translationY", dpToPx(-52, context));
        animation.setDuration(200);
        animation.start();
        Timber.i("Move up animation");
    }

    public static void moveDownAnimation(View view, Context context) {
        view.setTag(R.id.touched, false);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.overlay), "translationY", dpToPx(0, context));
        animation.setDuration(1000);
        animation.setStartDelay(2500);
        animation.start();
        Timber.i("Move down animation");
    }

}