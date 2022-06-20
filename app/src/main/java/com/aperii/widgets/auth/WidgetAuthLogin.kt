package com.aperii.widgets.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.app.AppFragment
import com.aperii.app.AppViewModel
import com.aperii.databinding.WidgetAuthLoginBinding
import com.aperii.stores.StoreAuth
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.tabs.WidgetTabsHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class WidgetAuthLogin : AppFragment(), KoinComponent {

    val binding: WidgetAuthLoginBinding by viewBinding(CreateMethod.INFLATE)
    val viewModel: WidgetAuthLoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.observeViewState().observe(this::configureUI)
        return binding.root
    }

    private fun configureUI(state: WidgetAuthLoginViewModel.ViewState) {
        configureBackButton()
        configureLoginButton()
        onLoginResult(state)
    }

    private fun configureBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            appActivity.onBackPressed()
        }
    }

    private fun configureLoginButton() {
        val error = binding.errorContainer
        binding.authButtonLogin.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (username.isBlank() || password.isBlank()) {
                error.visibility = View.VISIBLE
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }
    }

    private fun onLoginResult(state: WidgetAuthLoginViewModel.ViewState) {
        val error = binding.errorContainer
        when (state) {
            is WidgetAuthLoginViewModel.ViewState.Successful -> {
                requireContext().openScreen<WidgetTabsHost>(false)
            }
            is WidgetAuthLoginViewModel.ViewState.Unsuccessful -> {
                error.visibility = View.VISIBLE
            }
        }
    }

}

class WidgetAuthLoginViewModel(private val auth: StoreAuth) :
    AppViewModel<WidgetAuthLoginViewModel.ViewState>(ViewState.Uninitialized()) {

    open class ViewState {
        class Uninitialized : ViewState()
        class Successful : ViewState()
        class Unsuccessful : ViewState()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.login(username, password).let {
                updateViewState(
                    if (it != null)
                        ViewState.Successful()
                    else
                        ViewState.Unsuccessful()
                )
            }
        }
    }

}