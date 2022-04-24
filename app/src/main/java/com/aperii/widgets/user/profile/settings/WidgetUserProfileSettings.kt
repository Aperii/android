package com.aperii.widgets.user.profile.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetUserProfileSettingsBinding
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils.showToast
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager.Animations.SLIDE_FROM_RIGHT
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.user.profile.UserProfileHeaderViewModel
import org.koin.androidx.viewmodel.factory.DefaultViewModelFactory

class WidgetUserProfileSettings: AppFragment(R.layout.widget_user_profile_settings) {

    companion object {
        fun open(context: Context) = context.openScreen<WidgetUserProfileSettings>(animation = SLIDE_FROM_RIGHT)
    }

    lateinit var binding: WidgetUserProfileSettingsBinding
    private val viewModel: WidgetUserProfileSettingsViewModel by viewModels {
        ViewModelProvider.NewInstanceFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = WidgetUserProfileSettingsBinding.bind(view)
        viewModel.observeViewState().observe(this::configureUI)
    }

    fun configureUI(state: WidgetUserProfileSettingsViewModel.ViewState) {
        configureToolbar()
        configureHeader(state)
        configureBio(state)
        configureDisplayName(state)
        configurePronouns(state)
        binding.saveBtn.setOnClickListener {
            viewModel.save()
        }
        when(state) {
            is WidgetUserProfileSettingsViewModel.ViewState.Saved -> {
                if(state.errors.isNullOrEmpty()) appActivity.onBackPressed()
            }
        }
    }

    private fun configureBio(state: WidgetUserProfileSettingsViewModel.ViewState) = binding.bioEtLayout.apply {
        when(state) {
            is WidgetUserProfileSettingsViewModel.ViewState.Uninitialized -> {
                editText?.setText(viewModel.me?.bio)
                editText?.addTextChangedListener {
                    viewModel.changedBio = if(it?.toString() ?: "" == viewModel.me?.bio) null else it?.toString() ?: ""
                    isErrorEnabled = false
                }
            }
            is WidgetUserProfileSettingsViewModel.ViewState.Saved -> {
                state.errors?.firstOrNull {
                    it.field == "bio"
                }.let {
                    error = it?.error
                }
            }
        }
    }

    private fun configureDisplayName(state: WidgetUserProfileSettingsViewModel.ViewState) = binding.displayNameEtLayout.apply {
        when(state) {
            is WidgetUserProfileSettingsViewModel.ViewState.Uninitialized -> {
                editText?.setText(viewModel.me?.displayName)
                editText?.addTextChangedListener {
                    viewModel.changedDisplayName = if(it?.toString() ?: "" == viewModel.me?.displayName) null else it?.toString() ?: ""
                    isErrorEnabled = false
                }
            }
            is WidgetUserProfileSettingsViewModel.ViewState.Saved -> {
                state.errors?.firstOrNull {
                    it.field == "displayName"
                }.let {
                    error = it?.error
                }
            }
        }
    }

    private fun configureHeader(state: WidgetUserProfileSettingsViewModel.ViewState) = binding.header.run {
        when(state) {
            is WidgetUserProfileSettingsViewModel.ViewState.Uninitialized -> updateViewState(UserProfileHeaderViewModel.ViewState.Loaded(viewModel.me!!, true))
            is WidgetUserProfileSettingsViewModel.ViewState.Saved -> updateViewState(UserProfileHeaderViewModel.ViewState.Loaded(state.profile, true))
        }
    }

    private fun configurePronouns(state: WidgetUserProfileSettingsViewModel.ViewState) = binding.pronounsEtLayout.apply {
        when(state) {
            is WidgetUserProfileSettingsViewModel.ViewState.Uninitialized -> {
                editText?.setText(viewModel.me?.pronouns)
                editText?.addTextChangedListener {
                    viewModel.changedPronouns = if(it?.toString() ?: "" == viewModel.me?.pronouns) null else it?.toString() ?: ""
                    isErrorEnabled = false
                }
            }
            is WidgetUserProfileSettingsViewModel.ViewState.Saved -> {
                state.errors?.firstOrNull {
                    it.field == "pronouns"
                }.let {
                    error = it?.error
                }
            }
        }
    }

    private fun configureToolbar() = binding.toolbar.run {
        setHomeAsUpAction {
            appActivity.onBackPressed()
        }
        hideAvatar()
        subtitle = context.getString(R.string.username, viewModel.me?.username)
    }

}