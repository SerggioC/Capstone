package com.sergiocruz.capstone.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sergiocruz.capstone.R;

public class BindingAdapters {

    @BindingAdapter("circularImageUrl")
    //@BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void setCircularImageUrl(ImageView imageView, String url) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .circleCrop();

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);

        if (url == null || url.equals("null")) {
            Drawable placeHolder = ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_user_icon_48dp);
            imageView.setImageDrawable(placeHolder);
        }

    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);

        if (url == null || url.equals("null")) {
            Drawable placeHolder = ContextCompat.getDrawable(imageView.getContext(), R.drawable.travel_image);
            imageView.setImageDrawable(placeHolder);
        }

    }


}
