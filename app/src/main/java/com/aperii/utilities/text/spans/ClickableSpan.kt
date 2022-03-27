package com.aperii.utilities.text.spans

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ClickableSpan(
    private val color: Int,
    private val underline: Boolean,
    private val onClickListener: ((View) -> Unit)?,
    private val onLongPress: ((View) -> Unit)?
) : ClickableSpan() {

    override fun onClick(view: View) {
        onClickListener?.invoke(view)
    }

    fun onLongPress(view: View) {
        onLongPress?.invoke(view)
    }

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.isUnderlineText = underline
        textPaint.color = color
    }
}