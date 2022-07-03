package com.aperii.utilities.text.nodes

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import com.aperii.BuildConfig
import com.aperii.stores.StoreEmojis
import com.discord.simpleast.core.node.Node
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EmojiNode<RC : BaseRenderContext>(private val match: String) : Node<RC>(), KoinComponent {

    private val emojis: StoreEmojis by inject()

    override fun render(builder: SpannableStringBuilder, renderContext: RC) {
        val align = if (Build.VERSION.SDK_INT >= 29) 2 else DynamicDrawableSpan.ALIGN_BASELINE
        val drawable = BitmapDrawable(emojis.map[match]?.let {
            renderContext.context.getResId(
                it
            )
        }?.let { renderContext.context.resources.openRawResource(it) })
        drawable.setBounds(
            0,
            0,
            (renderContext.lineHeight * 0.9f).toInt(),
            (renderContext.lineHeight * 0.9f).toInt()
        )
        val span = ImageSpan(
            drawable,
            align
        )
        val i = builder.length
        builder.append(match)
        builder.setSpan(span, i, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun Context.getResId(emojiCode: String) =
        resources.getIdentifier(emojiCode, "raw", BuildConfig.APPLICATION_ID)

}