package com.aperii.stores

import android.content.Context
import com.aperii.utilities.Settings
import com.aperii.utilities.settings.BasePreferenceStore

class StorePreferences(context: Context) :
    BasePreferenceStore(Settings(context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)))