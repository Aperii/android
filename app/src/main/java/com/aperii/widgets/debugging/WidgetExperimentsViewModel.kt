package com.aperii.widgets.debugging

import com.aperii.app.AppViewModel
import com.aperii.stores.Experiment
import com.aperii.stores.StoreExperiments
import com.aperii.utilities.settings.settings

class WidgetExperimentsViewModel(private val experiments: StoreExperiments) : AppViewModel<WidgetExperimentsViewModel.ViewState>() {

    open class ViewState {
        class Loaded : ViewState() {
            var experiments: MutableList<Experiment> = mutableListOf()
        }
    }

    init {
        updateViewState(ViewState())
        val loaded = ViewState.Loaded()
        loaded.experiments.addAll(experiments.experiments)
        updateViewState(loaded)
    }

}