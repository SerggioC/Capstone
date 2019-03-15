package com.sergiocruz.capstone.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet


class TrackableMotionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        //progress = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.addOnOffsetChangedListener(this)
    }
}