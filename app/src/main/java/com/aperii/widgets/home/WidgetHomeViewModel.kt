package com.aperii.widgets.home

import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe

class WidgetHomeViewModel : AppViewModel<WidgetHomeViewModel.ViewState>() {

    open class ViewState {
        class Loading : ViewState()

        class Loaded(val posts: List<Post>) : ViewState()
    }

    init {
        updateViewState(ViewState.Loading())
        RestAPI.INSTANCE.getFeed().observe {
            updateViewState(ViewState.Loaded(this))
        }
    }
}