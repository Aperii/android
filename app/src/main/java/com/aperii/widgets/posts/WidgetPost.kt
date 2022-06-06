package com.aperii.widgets.posts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetPostBinding
import com.aperii.models.threads.Thread.Companion.toThread
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.screens.extras
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.posts.list.WidgetPostList
import org.koin.androidx.viewmodel.ext.android.sharedStateViewModel

class WidgetPost : AppFragment(R.layout.widget_post) {

    lateinit var binding: WidgetPostBinding
    val viewModel: WidgetPostViewModel by sharedStateViewModel(state = { arguments ?: Bundle() })

    companion object {
        val EXTRA_POST by extras()

        fun open(context: Context, id: String) = Bundle().run {
            putString(EXTRA_POST, id)
            context.openScreen<WidgetPost>(data = this, animation = ScreenManager.Animations.SLIDE_FROM_RIGHT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.also {
        binding = WidgetPostBinding.bind(it)
        viewModel.observeViewState().observe(this::configureUI)
    }

    private fun configureUI(viewState: WidgetPostViewModel.ViewState) {
        configureToolbar()
        configureReplyFab()
        if(viewState is WidgetPostViewModel.ViewState.Loaded) {
            configureMainPost()
            configureReplies()
        }
    }

    private fun configureMainPost() {
        (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
            viewModel.post?.let {
                setSource(it.toThread(), null, true)
            }
        }
    }

    private fun configureReplies() {
        (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
            addPosts(viewModel.replies)
        }
    }

    private fun configureReplyFab() = binding.createPostFab.setOnClickListener {
        WidgetPostCreate.open(it.context, "", arguments?.getString(EXTRA_POST) ?: "")
    }

    private fun configureToolbar() = binding.toolbar.run {
        hideAvatar()
        setHomeAsUpAction {
            appActivity.onBackPressed()
        }
    }
}