package com.shinwan2.postmaker.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.shinwan2.postmaker.R
import kotlinx.android.synthetic.main.view_empty_state.view.emptyStateImageView
import kotlinx.android.synthetic.main.view_empty_state.view.emptyStateTextView

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_empty_state, this)

        val params = context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView)

        val drawable = params.getDrawable(R.styleable.EmptyStateView_my_emptyStateViewImage)
        if (drawable != null) {
            emptyStateImageView.setImageDrawable(drawable)
        } else {
            emptyStateImageView.visibility = View.GONE
        }

        emptyStateTextView.text =
            params.getString(R.styleable.EmptyStateView_my_emptyStateViewText)

        params.recycle()
    }
}
