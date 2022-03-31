package com.aperii.utilities.text.nodes

import android.text.SpannableStringBuilder
import android.text.Spanned
import com.aperii.R
import com.aperii.utilities.Utils
import com.aperii.utilities.color.ColorUtils.getThemedColor
import com.aperii.utilities.text.spans.ClickableSpan
import com.discord.simpleast.core.node.Node

class UrlNode<RC: BaseRenderContext>(private val url: String): Node<RC>() {
    private val maxUrlLength = 27

    override fun render(builder: SpannableStringBuilder, renderContext: RC) {
        val span = ClickableSpan(renderContext.context.getThemedColor(R.attr.mentionAndLink), false, {
            Utils.openUrl(url)
        }, null)
        val i = builder.length
        val shouldEllipsize = url.length > maxUrlLength
        builder.append(if(shouldEllipsize) url.subSequence(0, maxUrlLength) else url)
        if(shouldEllipsize) builder.append("…")
        builder.setSpan(span, i, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}