package com.aperii.widgets.user.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetProfileBinding
import com.aperii.models.threads.Thread
import com.aperii.utilities.Logger
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.screens.extras
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.posts.list.WidgetPostList
import com.aperii.widgets.tabs.TabbedFragment

class WidgetProfile : TabbedFragment() {

    private val profileViewModel by viewModels<WidgetProfileViewModel> {
        SavedStateViewModelFactory(null, this, arguments)
    }
    private lateinit var binding: WidgetProfileBinding

    companion object {
        val EXTRA_USER by extras()

        fun open(context: Context, id: String) = Bundle().run {
            putString(EXTRA_USER, id)
            openScreen<WidgetProfile>(context, allowBack = true, data = this, animation = ScreenManager.Animations.SLIDE_FROM_RIGHT)
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
                WidgetPostCreate.open(context)
            }
            is WidgetProfileViewModel.ViewState.Loaded -> setOnClickListener {
                WidgetPostCreate.open(context, if(viewState.user.id != profileViewModel.me.id) "@${viewState.user.username} " else "")
            }
        }
    }

    private fun configureUI(viewState: WidgetProfileViewModel.ViewState) {
        configurePostFab(viewState)

        if (viewState is WidgetProfileViewModel.ViewState.Loaded) {
            configureToolbar(viewState)
            try {
                (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
                    setSource(Thread.fromList(viewState.posts), viewState.user)
                }
            } catch (_: Throwable) {}
        }
    }

    override fun onTabSelected(previouslySelected: Boolean) {}
}