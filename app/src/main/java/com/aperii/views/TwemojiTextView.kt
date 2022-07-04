package com.aperii.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.aperii.utilities.text.Renderer
import com.aperii.utilities.text.nodes.PostRenderContext

class TwemojiTextView: AppCompatTextView {

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text?.let { Renderer.renderTwemoji(it, PostRenderContext(context, lineHeight)) }, BufferType.SPANNABLE)
    }

}