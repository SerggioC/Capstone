package com.sergiocruz.capstone.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.util.Utils;

public class PriceTagTextView extends android.support.v7.widget.AppCompatTextView {
    public PriceTagTextView(Context context) {
        super(context);
    }

    public PriceTagTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public PriceTagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setupView() {
        int paddingLeft = Utils.dpToPx(16, this.getContext());
        int paddingEnd = Utils.dpToPx(8, this.getContext());
        int paddingBottom = Utils.dpToPx(3, this.getContext());
        this.setPadding(paddingLeft, 0, paddingEnd, paddingBottom);
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setTextColor(ContextCompat.getColor(this.getContext(), R.color.white));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        this.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_tags));
    }

}
