package com.aperii.widgets.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.viewModelScope
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.app.AppViewModel
import com.aperii.rest.RestAPIParams
import com.aperii.stores.StoreAuth
import com.aperii.utilities.rest.AuthAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.tabs.WidgetTabsHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class WidgetAuthLogin : AppFragment(R.layout.widget_auth_login), KoinComponent {
    lateinit var root: View

    val viewModel: WidgetAuthLoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = super.onCreateView(inflater, container, savedInstanceState)!!
        viewModel.observeViewState().observe(this::configureUI)
        return root
    }

    private fun configureUI(state: WidgetAuthLoginViewModel.ViewState) {
        configureBackButton()
        configureLoginButton()
        onLoginResult(state)
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

            viewModel.login(username, password)
        }
    }

    private fun onLoginResult(state: WidgetAuthLoginViewModel.ViewState) {
        val error = root.findViewById<View>(R.id.error_container)
        when(state) {
            is WidgetAuthLoginViewModel.ViewState.Successful -> {
                requireContext().openScreen<WidgetTabsHost>(false)
            }
            is WidgetAuthLoginViewModel.ViewState.Unsuccessful -> {
                error.visibility = View.VISIBLE
            }
        }
    }

}

class WidgetAuthLoginViewModel(private val auth: StoreAuth): AppViewModel<WidgetAuthLoginViewModel.ViewState>(ViewState.Uninitialized()) {

    open class ViewState {
        class Uninitialized : ViewState()
        class Successful: ViewState()
        class Unsuccessful: ViewState()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.login(username, password).let {
                updateViewState(
                    if(it != null)
                        ViewState.Successful()
                    else
                        ViewState.Unsuccessful()
                )
            }
        }
    }

}