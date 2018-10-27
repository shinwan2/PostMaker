package com.shinwan2.postmaker.util

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout
import android.view.View

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

@BindingAdapter("isVisible")
fun setVisible(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible == true) View.VISIBLE else View.GONE
}