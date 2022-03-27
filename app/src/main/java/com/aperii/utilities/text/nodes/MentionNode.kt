package com.aperii.utilities.text.nodes

import android.text.SpannableStringBuilder
import android.text.Spanned
import com.aperii.R
import com.aperii.utilities.color.ColorUtils.getThemedColor
import com.aperii.utilities.text.spans.ClickableSpan
import com.aperii.widgets.user.profile.WidgetProfile
import com.discord.simpleast.core.node.Node

class MentionNode<RC: BaseRenderContext>(private val username: String): Node<RC>() {

    override fun render(builder: SpannableStringBuilder, renderContext: RC) {
        val span = ClickableSpan(renderContext.context.getThemedColor(R.attr.mentionAndLink), false, {
            WidgetProfile.open("@${username}")
        }, null)
        val i = builder.length
        builder.append("@$username")
        builder.setSpan(span, i, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}