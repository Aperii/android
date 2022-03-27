package com.aperii.widgets.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.models.user.MeUser
import com.aperii.rest.RestAPIParams
import com.aperii.stores.StoreShelves
import com.aperii.utilities.rest.AuthAPI
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.tabs.WidgetTabsHost

class WidgetAuthLogin : AppFragment(R.layout.widget_auth_login) {

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
        configureBackButton()
        configureLoginButton()
    }

    private fun configureBackButton() {
        root.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            appActivity.onBackPressed()
        }
    }

    private fun configureLoginButton() {
        val error = root.findViewById<View>(R.id.error_container)
        root.findViewById<Button>(R.id.auth_button_login).setOnClickListener {
            val username = root.findViewById<EditText>(R.id.username_et).text.toString()
            val password = root.findViewById<EditText>(R.id.password_et).text.toString()
            if (username.isBlank() || password.isBlank()) {
                error.visibility = View.VISIBLE
                return@setOnClickListener
            }

            AuthAPI.getInstance().login(RestAPIParams.LoginBody(username, password))
                .observeAndCatch({
                    appActivity.prefs["APR_auth_tok"] = token
                    error.visibility = View.GONE
                    RestAPI.getInstance(token).getMe().observe {
                        StoreShelves.users.me = MeUser.fromApi(this)
                        appActivity.openScreen<WidgetTabsHost>(allowBack = false)
                    }
                }, {
                    error.visibility = View.VISIBLE
                    Log.e("Aperii", "Error logging in", this)
                })
        }

    }
}