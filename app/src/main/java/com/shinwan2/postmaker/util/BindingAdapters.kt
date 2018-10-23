package com.shinwan2.postmaker.util

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}