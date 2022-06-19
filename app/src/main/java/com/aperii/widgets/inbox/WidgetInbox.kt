package com.aperii.widgets.inbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.R
import com.aperii.databinding.WidgetInboxBinding
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.tabs.TabbedFragment

class WidgetInbox : TabbedFragment() {

    private lateinit var viewModel: WidgetInboxViewModel
    private val binding: WidgetInboxBinding by viewBinding(CreateMethod.INFLATE)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[WidgetInboxViewModel::class.java]
        viewModel.observeViewState().observe {
            configureUI(this)
        }
        return binding.root
    }

    private fun configureUI(state: WidgetInboxViewModel.ViewState) {
        binding.createPostFab.setOnClickListener {
            WidgetPostCreate.open(it.context)
        }
    }

    override fun onTabSelected(previouslySelected: Boolean) {}
}