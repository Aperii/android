package com.aperii.widgets.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.BuildConfig
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetSettingsBinding
import com.aperii.stores.StoreUsers
import com.aperii.utilities.rx.RxUtils.observe
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetSettings : AppFragment(), KoinComponent {

    private val binding: WidgetSettingsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WidgetSettingsViewModel by viewModel()
    private val users: StoreUsers by inject()

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
        configureSettingsItems()
    }

    private fun configureToolbar() {
        binding.toolbar.hideAvatar()
        binding.toolbar.setHomeAsUpAction {
            appActivity.onBackPressed()
        }
        binding.toolbar.subtitle = requireContext().getString(R.string.username, users.me?.username)
    }

    private fun configureSettingsItems() {
        binding.settingsItemInfo.description = "${requireContext().getString(R.string.app_name)} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

}