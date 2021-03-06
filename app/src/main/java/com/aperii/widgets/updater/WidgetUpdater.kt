package com.aperii.widgets.updater

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetUpdaterBinding
import com.aperii.utilities.Utils.runInThread
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.screens.extras
import com.aperii.utilities.update.UpdateUtils

class WidgetUpdater : AppFragment() {

    val binding: WidgetUpdaterBinding by viewBinding(CreateMethod.INFLATE)

    companion object {
        val EXTRA_RELEASE by extras()

        fun open(context: Context, release: UpdateUtils.Release?) = with(Bundle()) {
            putSerializable(EXTRA_RELEASE, release)
            context.openScreen<WidgetUpdater>(data = this)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val release = arguments?.getSerializable(EXTRA_RELEASE) as UpdateUtils.Release?
        configureUI(release != null, release)
        return binding.root
    }

    private fun configureUI(autoStart: Boolean, release: UpdateUtils.Release? = null) {
        binding.toolbar.hideAvatar()
        runInThread {
            if (autoStart) {
                UpdateUtils.downloadUpdate(binding.root.context, release!!, binding.updateProgress)
            } else {
                val (hasUpdate, rel) = UpdateUtils.checkUpdate()
                if (hasUpdate && rel != null) {
                    UpdateUtils.downloadUpdate(binding.root.context, rel, binding.updateProgress)
                }
            }
        }
    }

}