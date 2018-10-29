package com.shinwan2.postmaker.util

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator

private val COLOR_GENERATOR = ColorGenerator.MATERIAL

fun buildUserInitialCharImage(displayName: String, email: String): Drawable {
    return TextDrawable.builder()
        .beginConfig()
            .textColor(Color.WHITE)
            .toUpperCase()
            .bold()
        .endConfig()
        .buildRound(
            displayName.first().toString(),
            COLOR_GENERATOR.getColor(email)
        )
}