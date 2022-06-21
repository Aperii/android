package com.aperii.stores

import com.aperii.utilities.Settings
import com.aperii.utilities.settings.settings

class StoreExperiments {
    private val experimentStore by settings("StoreExperiments")

    val experiments = listOf(
        Experiment(
            "Settings Page",
            "settings_page_06_21_22",
            listOf("Bucket 0: No changes", "Bucket 1: Enable 'Settings' screen"),
            experimentStore
        ),
        Experiment(
            "Show Admin Tab",
            "admin_tab_03_13_22",
            listOf("Bucket 0: No changes", "Bucket 1: Show 'Admin' tab"),
            experimentStore
        )
    )

    operator fun get(id: String) = experiments.first { it.id == id }

}

data class Experiment(
    val name: String,
    val id: String,
    val buckets: List<String>,
    private val store: Settings
) {

    var bucket: Int
        get() = store[id, 0]
        set(value) = store.set(id, value)

}