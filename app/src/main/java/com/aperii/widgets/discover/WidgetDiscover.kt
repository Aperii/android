package com.aperii.widgets.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetDiscoverBinding
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.posts.create.WidgetPostCreate

class WidgetDiscover : AppFragment() {

    private lateinit var viewModel: WidgetDiscoverViewModel
    private lateinit var binding: WidgetDiscoverBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.widget_discover, container, false)
        binding = WidgetDiscoverBinding.bind(root)
        viewModel =
            ViewModelProvider(this).get(WidgetDiscoverViewModel::class.java)
        viewModel.observeViewState().observe {
            configureUI(this)
        }
        return root
    }

    private fun configureUI(state: WidgetDiscoverViewModel.ViewState) {
        binding.createPostFab.setOnClickListener {
            WidgetPostCreate.open(it.context)
        }
    }
}