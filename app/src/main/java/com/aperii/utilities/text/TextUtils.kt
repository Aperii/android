package com.aperii.utilities.text

import android.widget.TextView
import com.aperii.stores.StoreEmojis
import com.aperii.utilities.text.nodes.PostRenderContext
import kotlinx.coroutines.flow.combine
import okhttp3.internal.toHexString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TextUtils: KoinComponent {

    val emojis: StoreEmojis by inject()

    fun String.parseSurrogates(): String {
        val sb = StringBuilder(length)
        for (i in indices) {
            val char = this[i]
            if(char.code < 128)
                sb.append(char)
            else {
                if(char.isLowSurrogate() && emojis.twemojiList.contains(char.combine(this[i - 1]).toHexString())) {
                    sb.append("::")
                    sb.append(char.combine(this[i - 1]).toHexString())
                    sb.append("::")
                } else if (!char.isHighSurrogate()) sb.append(char)
            }
        }

        return sb.toString()
    }

    fun Char.combine(high: Char): Int {
        return ((high.code - 0xD800) * 0x400) + (code - 0xDC00) + 0x10000
    }

    fun TextView.renderPost(text: String) {
        setText(Renderer.render(text.parseSurrogates(), PostRenderContext(context, lineHeight)))
    }

}
