package com.sergiocruz.capstone.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class BindingAdapters {

    @BindingAdapter(value = {"circularImageUrl", "errorDrawable"}, requireAll = false)
    //@BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void setCircularImageUrl(ImageView imageView, String url, Drawable errorDrawable) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                /*.error(R.drawable.ic_user_icon_48dp)*/
                .error(errorDrawable)
                .circleCrop();

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);

//        if (url == null || url.equals("null")) {
////            Drawable placeHolder = ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_user_icon_48dp);
////            imageView.setImageDrawable(placeHolder);
//            Glide.with(imageView.getContext())
//                    .load(R.drawable.ic_user_icon_48dp)
//                    .apply(options)
//                    .into(imageView);
//        } else {
//
//
//        }

    }

  /*  @BindingAdapter("imageUrl")*/
    @BindingAdapter(value = {"imageUrl", "errorDrawable"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, Drawable errorDrawable) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                /*.error(R.drawable.travel_image)*/
                .error(errorDrawable)
                .priority(Priority.HIGH);

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);
//
//        if (url == null || url.equals("null")) {
//            Drawable placeHolder = ContextCompat.getDrawable(imageView.getContext(), R.drawable.travel_image);
//            imageView.setImageDrawable(placeHolder);
//        }

    }


}
