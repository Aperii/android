package com.aperii.utilities.color

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

object ColorUtils {

    fun Context.getThemedColor(@AttrRes attrRes: Int): Int = TypedValue()
        .apply { theme.resolveAttribute(attrRes, this, true) }
        .data

}