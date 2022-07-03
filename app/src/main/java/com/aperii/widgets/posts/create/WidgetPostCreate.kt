package com.aperii.widgets.posts.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.api.post.Post
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetPostCreateBinding
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.screens.extras
import com.aperii.utilities.text.TextUtils.renderDefault
import com.aperii.widgets.posts.preview.WidgetPostPreview
import org.koin.androidx.viewmodel.ext.android.sharedStateViewModel

class WidgetPostCreate : AppFragment() {

    val viewModel: WidgetPostCreateViewModel by sharedStateViewModel(state = {
        arguments ?: Bundle()
    })
    val binding: WidgetPostCreateBinding by viewBinding(CreateMethod.INFLATE)

    companion object {
        val EXTRA_MESSAGE by extras()
        val EXTRA_CLOSE_ON_EXIT by extras()
        val REPLY_TO by extras()

        fun open(context: Context, text: String = "", replyTo: String = "") = Bundle().run {
            putString(EXTRA_MESSAGE, text)
            if (replyTo.isNotEmpty()) putString(REPLY_TO, replyTo)
            context.openScreen<WidgetPostCreate>(data = this)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.observeViewState().observe(this::configureUI)
        return binding.root
    }

    private fun configureUI(viewState: WidgetPostCreateViewModel.ViewState) {
        arguments?.getString(EXTRA_MESSAGE)?.run(binding.postText::setText)
        configureToolbar()
        configureAvatar()
        configurePostButton()
        if (viewState is WidgetPostCreateViewModel.ViewState.ReplyLoaded)
            configureReplyTo(viewState.replyTo)
    }

    private fun configureToolbar() = binding.toolbar.apply {
        val closeOnExit = arguments?.getBoolean(EXTRA_CLOSE_ON_EXIT) == true
        setHomeAsUpAction {
            if (closeOnExit) appActivity.finishAffinity() else appActivity.onBackPressed()
        }
        if (closeOnExit) navigationIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_close_24dp)!!
        hideAvatar()
    }

    private fun configurePostButton() = binding.postBtn.setOnClickListener {
        val text = binding.postText.text.toString()
        binding.postBtn.isLoading = true
        viewModel.post(text, arguments?.getString(REPLY_TO) ?: "")
        binding.postBtn.isLoading = false
        appActivity.onBackPressed()
    }

    private fun configureAvatar() = viewModel.me.run {
        binding.avatar.load(if (avatar.isNotEmpty()) "https://api.aperii.com/cdn/avatars/${avatar}" else "https://aperii.com/av.png") {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.img_default_avatar)
        }
    }

    private fun configureReplyTo(replyTo: Post) {
        binding.replyHeader.visibility = View.VISIBLE
        binding.replyHeader.setOnClickListener {
            WidgetPostPreview.open(parentFragmentManager, replyTo.id)
        }
        binding.replyToText.run {
            renderDefault(context.getString(R.string.replyTo, replyTo.author.username))
        }
    }
}