package com.aperii.widgets.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aperii.R
import com.aperii.databinding.WidgetHomeBinding
import com.aperii.models.threads.Thread
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.posts.list.WidgetPostList
import com.aperii.widgets.tabs.TabbedFragment
import com.aperii.widgets.updater.WidgetUpdateDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class WidgetHome : TabbedFragment() {

    private val homeViewModel: WidgetHomeViewModel by viewModel()
    private lateinit var root: View
    lateinit var binding: WidgetHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            is WidgetHomeViewModel.ViewState.Loaded -> {
                try {
                    (childFragmentManager.findFragmentById(R.id.post_list_fragment) as WidgetPostList?)?.apply {
                        if(viewState.posts.isNotEmpty()) setSource(Thread.fromList(viewState.posts), null)
                    }
                } catch (_: Throwable) {}
                if(viewState.showUpdateDialog) {
                    WidgetUpdateDialog(requireContext(), homeViewModel.update!!).show()
                }
            }
        }
    }

    override fun onTabSelected(previouslySelected: Boolean) {}

}