package com.aperii.widgets.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.BuildConfig
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetSettingsAppInfoBinding
import com.aperii.databinding.WidgetSettingsBinding
import com.aperii.stores.StoreUsers
import com.aperii.utilities.rx.RxUtils.observe
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetSettingsAppInfo : AppFragment(), KoinComponent {

    private val binding: WidgetSettingsAppInfoBinding by viewBinding(CreateMethod.INFLATE)
    private val users: StoreUsers by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        configureUI()
        return binding.root
    }

    private fun configureUI() {
        configureToolbar()
        configureOssOption()
    }

    private fun configureToolbar() {
        binding.toolbar.hideAvatar()
        binding.toolbar.setHomeAsUpAction {
            appActivity.onBackPressed()
        }
        binding.toolbar.subtitle = "${requireContext().getString(R.string.app_name)} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    private fun configureOssOption() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.oss_licenses))
        binding.settingsItemOssl.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }
    }

}