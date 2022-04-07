package com.aperii.widgets.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetHomeBinding
import com.aperii.models.threads.Thread
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.posts.list.WidgetPostList

class WidgetHome : AppFragment() {

    private lateinit var homeViewModel: WidgetHomeViewModel
    private lateinit var root: View
    lateinit var binding: WidgetHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(WidgetHomeViewModel::class.java)
        root = inflater.inflate(R.layout.widget_home, container, false)
        homeViewModel.observeViewState().observe {
            configureUI(this)
        }
        binding = WidgetHomeBinding.bind(root)
        return root
    }

    private fun configureUI(viewState: WidgetHomeViewModel.ViewState) {
        binding.createPostFab.setOnClickListener {
            WidgetPostCreate.open(it.context)
        }
        when (viewState) {
            is WidgetHomeViewModel.ViewState.Loaded -> (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList).apply {
                setSource(Thread.fromList(viewState.posts), null)
            }
        }

    }
}