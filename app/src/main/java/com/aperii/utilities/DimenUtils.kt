package com.aperii.utilities

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.aperii.utilities.Utils.appContext

object DimenUtils {
    private val density = appContext.resources.displayMetrics.density

    val Int.dp: Int
        get() = (this * density + 0.5f).toInt()

    fun Context.getResizedDrawable(@DrawableRes drawable: Int, w: Int, h: Int = w) = ContextCompat.getDrawable(this, drawable)?.apply {
        mutate()
        setBounds(0, 0, w.dp, h.dp)
    }

}