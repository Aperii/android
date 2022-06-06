package com.aperii.widgets.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetSplashBinding

class WidgetSplash : AppFragment(R.layout.widget_splash) {

    lateinit var binding: WidgetSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding = WidgetSplashBinding.bind(view!!)
        configureUI()
        return view
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