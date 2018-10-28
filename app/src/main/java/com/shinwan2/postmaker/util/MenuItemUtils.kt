package com.shinwan2.postmaker.util

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.MenuItem

fun MenuItem.tintWithColorStateList(
    context: Context,
    @ColorRes colorStateListRes: Int
) {
    this.icon = this.icon.tintWithColorStateList(
        ContextCompat.getColorStateList(context, colorStateListRes)
    )
}