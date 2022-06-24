package com.aperii.stores

import android.content.Context
import com.aperii.utilities.text.listRawFiles

class StoreEmojis(context: Context) {

    val twemojiList by lazy { context.listRawFiles().map { it.replace("::", "") } }

}