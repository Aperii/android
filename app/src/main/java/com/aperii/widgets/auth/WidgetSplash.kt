package com.aperii.widgets.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetSplashBinding

class WidgetSplash : AppFragment(R.layout.widget_splash) {

    val binding: WidgetSplashBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        configureUI()
        return binding.root
    }

    private fun configureUI() {
        binding.materialButton.setOnClickListener {
            it.isEnabled = false
            appActivity.viewModel.checkAuth()
        }
    }

    private fun toggleReloadButton(canReload: Boolean) {
        binding.materialButton.apply {
            visibility = if (canReload) View.VISIBLE else View.GONE
            isEnabled = canReload
        }
        binding.msg.visibility = if (canReload) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.getBoolean("canRefresh").run(this::toggleReloadButton)
    }

}