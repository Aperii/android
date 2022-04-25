package com.aperii.widgets.debugging

import androidx.lifecycle.SavedStateHandle
import com.aperii.app.AppViewModel
import com.aperii.utilities.settings.settings

class WidgetExperimentsViewModel : AppViewModel<WidgetExperimentsViewModel.ViewState>() {
    val prefs by settings("StoreExperiments")

    private val experiments = listOf(
        Experiment(
            "Show Admin Tab",
            "admin_tab_03_13_22",
            listOf("Bucket 0: No changes", "Bucket 1: Show 'Admin' tab"),
            0
        )
    )

    data class Experiment(
        val name: String,
        val id: String,
        val buckets: List<String>,
        var bucket: Int
    )

    open class ViewState {
        class Loaded : ViewState() {
            var experiments: MutableList<Experiment> = mutableListOf()
        }
    }

    init {
        updateViewState(ViewState())
        val loaded = ViewState.Loaded()
        experiments.forEach {
            it.bucket = prefs[it.id, 0]
            loaded.experiments.add(it)
        }
        updateViewState(loaded)
    }

}