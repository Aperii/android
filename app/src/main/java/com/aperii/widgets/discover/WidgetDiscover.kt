package com.aperii.widgets.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.databinding.WidgetDiscoverBinding
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.tabs.TabbedFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class WidgetDiscover : TabbedFragment() {

    private val viewModel: WidgetDiscoverViewModel by viewModel()
    private val binding: WidgetDiscoverBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.observeViewState().observe {
            configureUI(this)
        }
        return binding.root
    }

    private fun configureUI(state: WidgetDiscoverViewModel.ViewState) {
        binding.createPostFab.setOnClickListener {
            WidgetPostCreate.open(it.context)
        }
    }

    override fun onTabSelected(previouslySelected: Boolean) {}
}