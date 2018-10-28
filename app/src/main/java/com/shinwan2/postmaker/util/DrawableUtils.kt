package com.shinwan2.postmaker.util

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat

fun Drawable.tintWithColor(@ColorInt color: Int): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    return wrappedDrawable.mutate().also { DrawableCompat.setTint(it, color) }
}

fun Drawable.tintWithColorStateList(color: ColorStateList?): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    return wrappedDrawable.mutate().also { DrawableCompat.setTintList(it, color) }
}