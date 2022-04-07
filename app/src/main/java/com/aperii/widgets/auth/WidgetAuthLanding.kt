package com.aperii.widgets.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.utilities.Utils.showToast
import com.aperii.utilities.screens.ScreenManager.openScreen

class WidgetAuthLanding : AppFragment(R.layout.widget_auth_landing) {

    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = super.onCreateView(inflater, container, savedInstanceState)!!
        configureUI()

        return root
    }

    private fun configureUI() {
        configureSignupButton()
        configureLoginButton()
    }

    private fun configureSignupButton() {
        root.findViewById<Button>(R.id.auth_button_signup).setOnClickListener {
            it.context.showToast("Currently Unavailable")
        }
    }

    private fun configureLoginButton() {
        root.findViewById<Button>(R.id.auth_button_login).setOnClickListener {
            appActivity.openScreen<WidgetAuthLogin>(allowBack = true)
        }
    }


}