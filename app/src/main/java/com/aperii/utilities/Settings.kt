package com.aperii.utilities

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Serializable

class Settings(val prefs: SharedPreferences) : Serializable {
    val gson: Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .serializeNulls()
        .setLenient()
        .create()

    inline operator fun <reified T> get(key: String, defaultValue: T) =
        prefs.all[key] as T? ?: defaultValue

    inline operator fun <reified T> set(key: String, value: T) = prefs.edit().run {
        when (value) {
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            else -> putString(key, gson.toJson(value))
        }
        apply()
    }

    fun clear(key: String? = null) = prefs.edit().run {
        if (key == null) clear() else remove(key)
        apply()
    }

    inline fun <reified T> getObject(key: String): T? {
        val str = prefs.all[key] as String? ?: return null
        return gson.fromJson(str, T::class.java)
    }

}