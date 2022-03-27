package com.aperii.widgets.posts.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetPostCreateBinding
import com.aperii.rest.RestAPIParams
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen

class WidgetPostCreate : AppFragment() {

    lateinit var viewModel: WidgetPostCreateViewModel
    lateinit var binding: WidgetPostCreateBinding

    companion object {
        const val EXTRA_MESSAGE = "com.aperii.intents.extras.MESSAGE"
        const val EXTRA_CLOSE_ON_EXIT = "com.aperii.intents.extras.CLOSE_ON_EXIT"

        fun open(text: String = "") = Bundle().run {
            putString(EXTRA_MESSAGE, text)
            Utils.appActivity.openScreen<WidgetPostCreate>(allowBack = true, animation = ScreenManager.Animations.SCALE_CENTER, data = this)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.widget_post_create, container, false)
        viewModel = ViewModelProvider(this)[WidgetPostCreateViewModel::class.java]
        binding = WidgetPostCreateBinding.bind(view)
        viewModel.observeViewState().observe(this::configureUI)
        return view
    }

    private fun configureUI(viewState: WidgetPostCreateViewModel.ViewState) {
        binding.toolbar.apply {
            val closeOnExit = arguments?.getBoolean(EXTRA_CLOSE_ON_EXIT) == true
            setHomeAsUpAction {
                if (closeOnExit) appActivity.finishAffinity() else appActivity.onBackPressed()
            }
            if (closeOnExit) navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_close_24dp)!!
            hideAvatar()
        }
        arguments?.getString(EXTRA_MESSAGE)?.run(binding.postText::setText)
        binding.postBtn.setOnClickListener {
            val text = binding.postText.text.toString()
            binding.postBtn.isLoading = true
            RestAPI.INSTANCE.createPost(RestAPIParams.PostBody(text)).observeAndCatch({
                appActivity.onBackPressed()
            }) {
                Utils.showToast("An error occurred while making that post")
                binding.postBtn.isLoading = false
                Logger("Error").error("An error has occurred", this)
            }
        }
        viewModel.me.run {
            binding.avatar.load(if (avatar.isNotEmpty()) "https://api.aperii.com/cdn/avatars/${avatar}" else "https://aperii.com/av.png") {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.img_default_avatar)
            }
        }
    }
}