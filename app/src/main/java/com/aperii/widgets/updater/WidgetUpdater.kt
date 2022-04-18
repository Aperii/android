package com.aperii.widgets.updater

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetUpdaterBinding
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils.runInThread
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.screens.extras
import com.aperii.utilities.update.UpdateUtils

class WidgetUpdater: AppFragment() {

    lateinit var binding: WidgetUpdaterBinding

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
        binding = WidgetUpdaterBinding.bind(layoutInflater.inflate(R.layout.widget_updater, container, false))
        val release = arguments?.getSerializable(EXTRA_RELEASE) as UpdateUtils.Release?
        configureUI(release != null, release)
        return binding.root
    }

    private fun configureUI(autoStart: Boolean, release: UpdateUtils.Release? = null) {
        binding.toolbar.hideAvatar()
        runInThread {
            if(autoStart) {
                binding.updateProgress.max = release!!.assets[0].size.toInt()
                UpdateUtils.downloadUpdate(binding.root.context, release, binding.updateProgress)
            } else {
                val (hasUpdate, rel) = UpdateUtils.checkUpdate()
                if (hasUpdate && rel != null) {
                    binding.updateProgress.max = rel.assets[0].size.toInt()
                    UpdateUtils.downloadUpdate(binding.root.context, rel, binding.updateProgress)
                }
            }
        }
    }

}