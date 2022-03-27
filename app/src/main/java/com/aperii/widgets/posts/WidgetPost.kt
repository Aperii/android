package com.aperii.widgets.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetPostBinding
import com.aperii.models.threads.Thread.Companion.toThread
import com.aperii.stores.StoreShelves
import com.aperii.utilities.Utils
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.posts.list.WidgetPostList

class WidgetPost : AppFragment(R.layout.widget_post) {

    lateinit var binding: WidgetPostBinding

    companion object {
        const val EXTRA_POST = "com.aperii.intents.extras.EXTRA_POST"

        fun open(id: String) = Bundle().run {
            putString(EXTRA_POST, id)
            Utils.appActivity.openScreen<WidgetPost>(allowBack = true, data = this)
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
        arguments?.getString(EXTRA_POST)?.run {
            (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
                StoreShelves.posts.fetchPost(this@run)?.run {
                    setSource(toThread(), null, true)
                }
            }
        }
    }

    private fun configureToolbar() = binding.toolbar.run {
        hideAvatar()
        setHomeAsUpAction {
            appActivity.onBackPressed()
        }
    }
}