package com.aperii.widgets.settings

import com.aperii.app.AppViewModel
import com.aperii.stores.StorePreferences

class WidgetSettingsViewModel(
    private val prefs: StorePreferences
) : AppViewModel<WidgetSettingsViewModel.ViewState>(ViewState()) {

    class ViewState

}