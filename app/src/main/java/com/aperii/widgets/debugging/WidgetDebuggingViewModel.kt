package com.aperii.widgets.debugging

import com.aperii.app.AppViewModel
import com.aperii.utilities.Logger

class WidgetDebuggingViewModel : AppViewModel<WidgetDebuggingViewModel.ViewState>() {

    open class ViewState {
        class Loaded(val logs: MutableList<Logger.LoggedItem>) : ViewState()
    }

    init {
        updateViewState(ViewState.Loaded(Logger.logs))
    }
}