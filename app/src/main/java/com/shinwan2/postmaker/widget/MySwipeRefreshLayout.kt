package com.shinwan2.postmaker.widget

import android.content.Context
import android.graphics.Color
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import com.shinwan2.postmaker.R

class MySwipeRefreshLayout(
    context: Context,
    attrs: AttributeSet?
) : SwipeRefreshLayout(context, attrs) {
    init {
        val params = context.obtainStyledAttributes(attrs, R.styleable.MySwipeRefreshLayout)
        val color = params.getColor(
            R.styleable.MySwipeRefreshLayout_my_swipeRefreshIndicatorColor,
            Color.BLACK
        )
        setColorSchemeColors(color)
        val background = params.getColor(
            R.styleable.MySwipeRefreshLayout_my_swipeRefreshIndicatorBackground,
            Color.WHITE
        )
        setProgressBackgroundColorSchemeColor(background)
        params.recycle()
    }
}
