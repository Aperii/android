package com.aperii.widgets.settings

import com.aperii.app.AppViewModel
import com.aperii.stores.StorePreferences
import com.aperii.stores.Theme

class WidgetSettingsViewModel(private val prefs: StorePreferences) : AppViewModel<WidgetSettingsViewModel.ViewState>(ViewState()) {

    class ViewState

    fun toggleTheme() {
        prefs.theme = if(prefs.theme == Theme.LIGHT) Theme.DARK else Theme.LIGHT
    }

}