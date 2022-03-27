package com.aperii.utilities

import com.aperii.utilities.Utils.appContext

object DimenUtils {
    private val density = appContext.resources.displayMetrics.density

    val Int.dp: Int
        get() = (this * density + 0.5f).toInt()
}