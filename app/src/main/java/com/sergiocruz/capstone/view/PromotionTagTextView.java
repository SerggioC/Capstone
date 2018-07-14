package com.sergiocruz.capstone.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.util.Utils;

public class PromotionTagTextView extends AppCompatTextView {
    public PromotionTagTextView(Context context) {
        super(context);
    }

    public PromotionTagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public PromotionTagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setupView() {
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        this.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        this.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_discount));
    }

    /**
     * Sets the text to be displayed and the {@link BufferType}.
     * <p/>
     * When required, TextView will use {@link Spannable.Factory} to create final or
     * intermediate {@link Spannable Spannables}. Likewise it will use
     * {@link Editable.Factory} to create final or intermediate
     * {@link Editable Editables}.
     *
     * @param text text to be displayed
     * @param type a {@link BufferType} which defines whether the text is
     *             stored as a static text, styleable/spannable text, or editable text
     * @attr ref android.R.styleable#TextView_text
     * @attr ref android.R.styleable#TextView_bufferType
     * @see #setText(CharSequence)
     * @see BufferType
     * @see #setSpannableFactory(Spannable.Factory)
     * @see #setEditableFactory(Editable.Factory)
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (text.length() > 4) {
            setViewPadding(16, 16, 17, 30);
        } else {
            setViewPadding(16, 14, 17, 29);
        }

    }

    private void setViewPadding(int left, int top, int right, int bottom) {
        Context context = this.getContext();
        int paddingLeft = Utils.dpToPx(left, context);
        int paddingTop = Utils.dpToPx(top, context);
        int paddingEnd = Utils.dpToPx(right, context);
        int paddingBottom = Utils.dpToPx(bottom, context);
        super.setPadding(paddingLeft, paddingTop, paddingEnd, paddingBottom);
    }
}
