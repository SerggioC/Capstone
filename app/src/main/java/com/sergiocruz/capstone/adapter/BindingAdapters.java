package com.sergiocruz.capstone.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
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
                .transition(withCrossFade())
                .into(imageView);

    }

    //@BindingAdapter("imageUrl")
    @BindingAdapter(value = {"imageUrl", "errorDrawable", "cornerRadius"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, Drawable errorDrawable, Float cornerRadius) {
        // dp gets converted to density * value automatically here

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(errorDrawable)
                .priority(Priority.HIGH);

        if (cornerRadius != null) {
            options = options.transforms(new CenterCrop(), new RoundedCorners(Math.round(cornerRadius)));
        }

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .transition(withCrossFade())
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
