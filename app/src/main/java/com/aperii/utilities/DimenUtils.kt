package com.aperii.utilities

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

object DimenUtils {
    var density by Delegates.notNull<Float>()

    val Int.dp: Int
        get() = (this * density + 0.5f).toInt()

    fun Context.getResizedDrawable(@DrawableRes drawable: Int, w: Int, h: Int = w) = ContextCompat.getDrawable(this, drawable)?.apply {
        mutate()
        setBounds(0, 0, w.dp, h.dp)
    }

}