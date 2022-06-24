package com.aperii.utilities.text

import android.content.Context
import com.aperii.R
import com.google.gson.Gson


fun Context.loadEmojiRegex(): String {
    val builder = StringBuilder()
    builder.append("^(")
    builder.append(listRawFiles().joinToString("|"))
    builder.append(")")
    println(builder.toString())
    return builder.toString()
}

fun Context.listRawFiles(): List<String> {
    val list = mutableListOf<String>()
    for (field in R.raw::class.java.fields)
        list.add("::" + field.name.replaceFirst("emoji", "") + "::")
    return list
}