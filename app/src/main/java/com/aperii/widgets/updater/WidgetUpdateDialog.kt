package com.aperii.widgets.updater

import android.app.AlertDialog
import android.content.Context
import com.aperii.BuildConfig
import com.aperii.R
import com.aperii.utilities.update.UpdateUtils

class WidgetUpdateDialog(context: Context, release: UpdateUtils.Release) : AlertDialog(context) {

    init {
        setTitle(R.string.new_update)
        setMessage(
            context.getString(
                R.string.version_change,
                "v${BuildConfig.VERSION_NAME}",
                release.versionName
            )
        )
        setCanceledOnTouchOutside(true)
        setButton(BUTTON_POSITIVE, context.getString(R.string.update_now)) { _, _ ->
            WidgetUpdater.open(context, release)
        }
    }

}