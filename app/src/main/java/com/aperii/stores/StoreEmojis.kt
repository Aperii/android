package com.aperii.stores

import android.content.Context
import com.aperii.models.domain.emoji.EmojiMap
import com.aperii.utilities.Utils.readJson
import okhttp3.internal.toHexString

class StoreEmojis(context: Context) {

    val map by lazy { context.assets.open("data/emoji.json").readJson<EmojiMap>().emoji }

    val regex by lazy {

        "^(${
            map.keys.sortedByDescending { it.length }.joinToString("|") { emoji ->
                emoji.toCharArray().joinToString("") { "\\x{${it.code.toHexString()}}" }
            }
        })"

    }

}