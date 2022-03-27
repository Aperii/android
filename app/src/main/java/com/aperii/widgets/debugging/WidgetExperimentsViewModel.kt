package com.aperii.widgets.debugging

import android.content.Context
import com.aperii.app.AppViewModel
import com.aperii.utilities.SettingsUtils
import com.aperii.utilities.Utils

class WidgetExperimentsViewModel : AppViewModel<WidgetExperimentsViewModel.ViewState>() {
    val prefs = SettingsUtils(
        Utils.appActivity.getSharedPreferences(
            "StoreExperiments",
            Context.MODE_PRIVATE
        )
    )
    val experiments = listOf<Experiment>(
        Experiment(
            "Show Groups Tab",
            "groups_tab_03_13_22",
            listOf("Bucket 0: No changes", "Bucket 1: Show 'Groups' tab"),
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