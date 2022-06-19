package com.aperii.widgets.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
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
    val binding: WidgetHomeBinding by viewBinding(CreateMethod.INFLATE)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel.observeViewState().observe {
            configureUI(this)
        }
        return binding.root
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