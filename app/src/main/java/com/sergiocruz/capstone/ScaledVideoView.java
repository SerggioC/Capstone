package com.sergiocruz.capstone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class ScaledVideoView extends VideoView {
    private float aspectRatio = 1f;

    public ScaledVideoView(Context context) {
        super(context);
    }

    public ScaledVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaledVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) (widthMeasureSpec * aspectRatio), heightMeasureSpec);
    }
}
