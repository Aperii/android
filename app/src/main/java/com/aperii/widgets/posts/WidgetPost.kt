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
import com.aperii.stores.StoreShelves
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.posts.list.WidgetPostList

class WidgetPost : AppFragment(R.layout.widget_post) {

    lateinit var binding: WidgetPostBinding

    companion object {
        const val EXTRA_POST = "com.aperii.intents.extras.EXTRA_POST"

        fun open(context: Context, id: String) = Bundle().run {
            putString(EXTRA_POST, id)
            openScreen<WidgetPost>(context, allowBack = true, data = this, animation = ScreenManager.Animations.SLIDE_FROM_RIGHT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.also {
        binding = WidgetPostBinding.bind(it)
        configureUI()
    }

    private fun configureUI() {
        configureToolbar()
        configureReplyFab()
        arguments?.getString(EXTRA_POST)?.run {
            (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
                StoreShelves.posts.fetchPost(this@run)?.run {
                    setSource(toThread(), null, true)
                }
            }
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