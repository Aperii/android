package com.aperii.utilities.text

import android.widget.TextView
import com.aperii.utilities.text.nodes.PostRenderContext

object TextUtils {

    fun TextView.renderPost(text: String) {
        setText(Renderer.render(text, PostRenderContext(context)))
    }

}