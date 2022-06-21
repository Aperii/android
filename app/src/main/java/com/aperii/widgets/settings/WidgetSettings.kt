package com.aperii.widgets.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetSettingsBinding
import com.aperii.utilities.rx.RxUtils.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

class WidgetSettings : AppFragment() {

    private val binding: WidgetSettingsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WidgetSettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.observeViewState().observe(::configureUI)
        return binding.root
    }

    private fun configureUI(state: WidgetSettingsViewModel.ViewState) {
        configureToolbar()
    }

    private fun configureToolbar() {
        binding.toolbar.hideAvatar()
        binding.toolbar.setHomeAsUpAction {
            appActivity.onBackPressed()
        }
    }

}