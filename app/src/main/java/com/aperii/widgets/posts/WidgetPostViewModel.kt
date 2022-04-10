package com.aperii.widgets.posts

import androidx.lifecycle.SavedStateHandle
import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.user.profile.WidgetProfile

class WidgetPostViewModel(state: SavedStateHandle): AppViewModel<WidgetPostViewModel.ViewState>(ViewState.Loading()) {

    open class ViewState {
        class Loading : ViewState()
        data class Loaded(val replies: List<Post>) : ViewState()
    }

    init {
        state.get<String?>(WidgetPost.EXTRA_POST)?.run {
            RestAPI.INSTANCE.getReplies(this).observe {
                updateViewState(ViewState.Loaded(this))
            }
        }
    }

}