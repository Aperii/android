package com.aperii.widgets.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetAuthLandingBinding
import com.aperii.utilities.Utils.showToast
import com.aperii.utilities.screens.ScreenManager.openScreen

class WidgetAuthLanding : AppFragment() {

    val binding: WidgetAuthLandingBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        configureUI()
        return binding.root
    }

    private fun configureUI() {
        configureSignupButton()
        configureLoginButton()
    }

    private fun configureSignupButton() {
        binding.authButtonSignup.setOnClickListener {
            it.context.showToast("Currently Unavailable")
        }
    }

    private fun configureLoginButton() {
        binding.authButtonLogin.setOnClickListener {
            appActivity.openScreen<WidgetAuthLogin>(allowBack = true)
        }
    }


}