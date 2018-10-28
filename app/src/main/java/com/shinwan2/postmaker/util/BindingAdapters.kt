package com.shinwan2.postmaker.util

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import java.util.ArrayDeque

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

@BindingAdapter("isVisible")
fun setVisible(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("isRecursiveEnabled")
fun setRecursiveEnabled(view: View, isEnabled: Boolean?) {
    val inspectedViews = ArrayDeque(listOf(view))
    while (!inspectedViews.isEmpty()) {
        val inspectedView = inspectedViews.poll()
        inspectedView.isEnabled = isEnabled == true
        if (inspectedView is ViewGroup) {
            (0 until inspectedView.childCount).mapTo(inspectedViews) {
                inspectedView.getChildAt(it)
            }
        }
    }
}

@BindingAdapter("isRefreshing")
fun setRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean?) {
    view.isRefreshing = isRefreshing ?: false
}