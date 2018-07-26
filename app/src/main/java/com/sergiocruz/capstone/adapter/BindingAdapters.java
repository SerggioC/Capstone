package com.sergiocruz.capstone.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sergiocruz.capstone.model.Status;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.sergiocruz.capstone.database.DateConverter.getFormattedDateString;
import static com.sergiocruz.capstone.model.Status.LOADING;
import static com.sergiocruz.capstone.model.Status.SUCCESS;

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
    @BindingAdapter(value = {"imageUrl", "errorDrawable", "cornerRadius", "imageList"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, Drawable errorDrawable, Float cornerRadius, List<String> imageList) {

        if (imageList != null) {
            int random = new Random().nextInt(imageList.size());
            url = imageList.get(random);
        }

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(errorDrawable)
                .priority(Priority.HIGH);

        // dp gets converted to density * value automatically here
        if (cornerRadius != null) {
            options = options.transforms(new CenterCrop(), new RoundedCorners(Math.round(cornerRadius)));
        }

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .transition(withCrossFade())
                .into(imageView);

    }

    // Set formatted date to textView
    @BindingAdapter("date")
    public static void setDate(TextView textView, Long dateMillis) {
        textView.setText(getFormattedDateString(dateMillis));
    }


    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Status status) {
        Integer visibility = View.GONE;
        if (status == LOADING) {
            visibility = View.VISIBLE;
        } else if (status == SUCCESS){
            visibility = View.GONE;
        }
        view.setVisibility(visibility);
    }

    //slower
    @BindingAdapter("visible")
    public static void setVisible(ImageView view, Status status) {
        Integer visibility = View.GONE;
        Drawable drawable = view.getDrawable();
        Animatable animatable = (Animatable) drawable;

        if (status == LOADING || status == Status.PROCESSING) {
            visibility = View.VISIBLE;
            animatable.start();
        } else if (status == SUCCESS){
            animatable.stop();
            visibility = View.GONE;
        }
        view.setVisibility(visibility);
    }

}
