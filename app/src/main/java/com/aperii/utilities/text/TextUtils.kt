package com.aperii.utilities.text

import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.aperii.utilities.text.nodes.PostRenderContext

object TextUtils {

    fun TextView.renderDefault(text: String) {
        setText(Renderer.render(text, PostRenderContext(context, lineHeight)))
    }

}
