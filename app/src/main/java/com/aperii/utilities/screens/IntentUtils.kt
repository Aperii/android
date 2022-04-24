package com.aperii.utilities.screens

import com.aperii.BuildConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Extra : ReadOnlyProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>) = "${BuildConfig.APPLICATION_ID}.intents.extras.${if(property.name.uppercase().startsWith("EXTRA_")) "" else "EXTRA_"}${property.name.uppercase()}"
}

fun extras() = Extra()