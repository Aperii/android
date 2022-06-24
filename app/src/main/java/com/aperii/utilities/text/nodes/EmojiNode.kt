package com.aperii.utilities.text.nodes

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.TypedValue
import com.aperii.BuildConfig
import com.aperii.utilities.text.TextUtils.parseSurrogates
import com.discord.simpleast.core.node.Node

class EmojiNode<RC : BaseRenderContext>(private val match: String) : Node<RC>() {

    override fun render(builder: SpannableStringBuilder, renderContext: RC) {
        val code = match.replace("::", "")
        val align = if (Build.VERSION.SDK_INT >= 29) 2 else DynamicDrawableSpan.ALIGN_BASELINE
        val drawable = BitmapDrawable(renderContext.context.resources.openRawResource(renderContext.context.getResId("emoji$code")))
        drawable.setBounds(0, 0, (renderContext.lineHeight * 0.9f).toInt(), (renderContext.lineHeight * 0.9f).toInt());
        val span = ImageSpan(
            drawable,
            align
        )
        val i = builder.length
        builder.append(match)
        builder.setSpan(span, i, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    
    private fun Context.getResId(emojiCode: String) = resources.getIdentifier(emojiCode, "raw", BuildConfig.APPLICATION_ID)

}