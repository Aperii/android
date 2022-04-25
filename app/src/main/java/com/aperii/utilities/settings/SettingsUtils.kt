package com.aperii.utilities.settings

import android.content.Context
import android.content.SharedPreferences
import com.aperii.BuildConfig
import com.aperii.utilities.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object SettingsUtils : KoinComponent {
    val context: Context by inject()

    class Sets(val name: String) : ReadOnlyProperty<Any, Settings> {
        override fun getValue(thisRef: Any, property: KProperty<*>) = Settings(context.getSharedPreferences(name, Context.MODE_PRIVATE))
    }
}

fun settings(name: String = "PREFS") = SettingsUtils.Sets(name)

