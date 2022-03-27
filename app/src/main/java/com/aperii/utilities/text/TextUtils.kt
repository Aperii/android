package com.aperii.utilities.text

import android.text.SpannableStringBuilder
import android.widget.TextView
import com.aperii.utilities.text.nodes.BaseRenderContext
import com.aperii.utilities.text.nodes.MentionNode
import com.aperii.utilities.text.nodes.PostRenderContext
import com.discord.simpleast.core.simple.SimpleRenderer

object TextUtils {

    fun TextView.renderPost(text: String) {
        setText(AperiiRenderer.render(text, PostRenderContext(context)))
    }

}