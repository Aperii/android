package com.aperii.widgets.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetProfileBinding
import com.aperii.models.threads.Thread
import com.aperii.utilities.Utils
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.posts.list.WidgetPostList

class WidgetProfile : AppFragment() {

    private val profileViewModel by viewModels<WidgetProfileViewModel> {
        SavedStateViewModelFactory(null, this, arguments)
    }
    private lateinit var binding: WidgetProfileBinding

    companion object {
        const val EXTRA_USER = "com.aperii.intents.extras.USER"

        fun open(id: String) = Bundle().run {
            putString(EXTRA_USER, id)
            Utils.appActivity.openScreen<WidgetProfile>(allowBack = true, data = this)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel.observeViewState().observe(this::configureUI)
        binding = WidgetProfileBinding.bind(inflater.inflate(R.layout.widget_profile, container, false))
        return binding.root
    }

    private fun configureToolbar(loaded: WidgetProfileViewModel.ViewState.Loaded) {
        toolbar?.apply {
            title = loaded.user.displayName
            subtitle = "${loaded.posts.size} Posts"
            if (loaded.user.flags.verified) showBadge() else showBadge(false)
        }
    }

    private fun configurePostFab(viewState: WidgetProfileViewModel.ViewState) = binding.createPostFab.run {
        when(viewState) {
            is WidgetProfileViewModel.ViewState.Loading -> setOnClickListener {
                WidgetPostCreate.open()
            }
            is WidgetProfileViewModel.ViewState.Loaded -> setOnClickListener {
                WidgetPostCreate.open(if(viewState.user.id != profileViewModel.me.id) "@${viewState.user.username} " else "")
            }
        }
    }

    private fun configureUI(viewState: WidgetProfileViewModel.ViewState) {
        configurePostFab(viewState)

        if (viewState is WidgetProfileViewModel.ViewState.Loaded) {
            configureToolbar(viewState)
            (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
                setSource(Thread.fromList(viewState.posts), viewState.user)
            }
        }
    }
}