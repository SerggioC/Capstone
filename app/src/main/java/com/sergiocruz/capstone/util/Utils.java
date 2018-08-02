package com.sergiocruz.capstone.util;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sergiocruz.capstone.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class Utils {

    private Utils() {}

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }

    public static AlertDialog.Builder buildNoNetworkDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.no_internet);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
        return builder;
    }

    /**
     * Get Bitmap from InputStream
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Timber.e("GetBitmap error %s", e);
            return null;
        }
    }

    /**
     * Get Bitmap with Glide; will cache for faster loading
     */
    public static Bitmap getBitmapFromURL(Context context, String url) {
        try {
            return Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int dpToPx(float dpValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static boolean upDownAnimation(View view, MotionEvent event) {
        boolean actionDown = false;

        int eventAction = event.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN: {
                actionDown = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                actionDown = true;
                break;
            }
        }

        View viewToMove = view.findViewById(R.id.overlay);
        Boolean touched = (Boolean) viewToMove.getTag(R.id.touched);
        if (touched == null) touched = false;
        if (actionDown && !touched) {
            Utils.moveUpAnimation(viewToMove);
        } else if (touched) {
            Utils.moveDownAnimation(viewToMove);
        }
        return false;
    }

    public static void moveUpAnimation(View view) {
        view.setTag(R.id.touched, true);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", dpToPx(-52, view.getContext()));
        animation.setDuration(200);
        animation.start();
        Timber.i("Move up animation");
    }

    public static void moveDownAnimation(View view) {
        view.setTag(R.id.touched, false);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", dpToPx(0, view.getContext()));
        animation.setDuration(1000);
        animation.setStartDelay(2500);
        animation.start();
        Timber.i("Move down animation");
    }

    public static void setItemViewAnimation(View viewToAnimate, int position) {
        Animation topAnimation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_from_top);
        topAnimation.setStartOffset(30 * position);
        viewToAnimate.startAnimation(topAnimation);
    }

    public static void zoomInAnimation(View view) {
        view.setTag(R.id.touched, true);
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.zoom_in_animation);
        animatorSet.setTarget(view);
        animatorSet.start();
    }

    public static void zoomOutAnimation(View view) {
        view.setTag(R.id.touched, false);
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.zoom_out_animation);
        animatorSet.setTarget(view);
        animatorSet.start();
    }

    public static void animateViewsOnPreDraw(View parent, View[] viewsToAnimate) {
        ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Interpolator interpolator = new DecelerateInterpolator();
                for (int i = 0; i < viewsToAnimate.length; i++) {
                    View view = viewsToAnimate[i];
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    view.setAlpha(0);
                    view.setScaleX(0.8f);
                    view.setScaleY(0.8f);
                    view.setTranslationY(100);
                    view.animate()
                            .setInterpolator(interpolator)
                            .alpha(1)
                            .scaleX(1)
                            .scaleY(1)
                            .translationY(0)
                            .setStartDelay(50 * (i + 1))
                            .start();
                }
                parent.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        parent.getViewTreeObserver().addOnPreDrawListener(listener);
    }

    public static void showSlimToast(Context context, String toastText, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        TextView layout = (TextView) inflater.inflate(R.layout.slim_toast_layout, null);
        layout.setText(toastText);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

}
