package com.aperii.widgets.inbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aperii.R
import com.aperii.databinding.WidgetInboxBinding
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.tabs.TabbedFragment

class WidgetInbox : TabbedFragment() {

    private lateinit var viewModel: WidgetInboxViewModel
    private lateinit var binding: WidgetInboxBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.widget_inbox, container, false)
        viewModel = ViewModelProvider(this)[WidgetInboxViewModel::class.java]
        binding = WidgetInboxBinding.bind(root)
        viewModel.observeViewState().observe {
            configureUI(this)
        }
        return root
    }

    private fun configureUI(state: WidgetInboxViewModel.ViewState) {
        binding.createPostFab.setOnClickListener {
            WidgetPostCreate.open(it.context)
        }
    }

    override fun onTabSelected(previouslySelected: Boolean) {}
}