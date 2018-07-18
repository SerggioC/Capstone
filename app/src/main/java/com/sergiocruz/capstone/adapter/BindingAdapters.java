package com.sergiocruz.capstone.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import static com.sergiocruz.capstone.database.DateConverter.getFormattedDateString;

public class BindingAdapters {

    @BindingAdapter(value = {"circularImageUrl", "errorDrawable"}, requireAll = false)
    public static void setCircularImageUrl(ImageView imageView, String url, Drawable errorDrawable) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .error(errorDrawable)
                .circleCrop();

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);

    }

    //@BindingAdapter("imageUrl")
    @BindingAdapter(value = {"imageUrl", "errorDrawable", "listener"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, Drawable errorDrawable, RequestListener<Drawable> listener) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(errorDrawable)
                .priority(Priority.HIGH);

        Glide.with(imageView.getContext())
                .load(url)
                .listener(listener)
                .apply(options)
                .into(imageView);

    }

    @BindingAdapter("date")
    public static void setDate(TextView textView, Long dateMillis) {
        textView.setText(getFormattedDateString(dateMillis));
    }

//
//    @BindingAdapter("android:visibility")
//    public static void setVisibility(View view, Boolean value) {
//        view.setVisibility(value ? View.VISIBLE : View.GONE);
//    }

}
