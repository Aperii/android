package com.aperii.utilities

import android.content.SharedPreferences
import org.koin.dsl.koinApplication
import java.io.Serializable

class Settings(val prefs: SharedPreferences) : Serializable {

    inline operator fun <reified T> get(key: String, defaultValue: T) =
        prefs.all[key] as T? ?: defaultValue

    inline operator fun <reified T> set(key: String, value: T) = prefs.edit().run {
        when (value) {
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            else -> throw NoWhenBranchMatchedException()
        }
        apply()
    }


    fun clear(key: String? = null) = prefs.edit().run {
        if (key == null) clear() else remove(key)
        apply()
    }

}