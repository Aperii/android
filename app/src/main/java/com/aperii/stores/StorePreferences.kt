package com.aperii.stores

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.aperii.BuildConfig
import com.aperii.utilities.Settings
import com.aperii.utilities.settings.BasePreferenceStore

class StorePreferences(context: Context) :
    BasePreferenceStore(Settings(context.getSharedPreferences("PREFS", Context.MODE_PRIVATE))) {

    var experimentsEnabled by booleanPreference("enable_experiments", BuildConfig.DEBUG)

    var theme by enumPreference("theme", Theme.DARK)

}

enum class Theme {
    LIGHT,
    DARK
}