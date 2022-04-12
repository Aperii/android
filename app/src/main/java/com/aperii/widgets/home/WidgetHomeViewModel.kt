package com.aperii.widgets.home

import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.utilities.Utils.runInThread
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.update.UpdateUtils

class WidgetHomeViewModel : AppViewModel<WidgetHomeViewModel.ViewState>() {
    var update: UpdateUtils.Release? = null
    open class ViewState {
        class Loading : ViewState()

        class Loaded(val posts: List<Post>, var showUpdateDialog: Boolean = false) : ViewState()
    }

    init {
        updateViewState(ViewState.Loading())
        RestAPI.INSTANCE.getFeed().observe {
            updateViewState(ViewState.Loaded(this))
        }
        runInThread {
            val (hasUpdate, release) = UpdateUtils.checkUpdate()
            update = release
            if(hasUpdate) updateViewState((viewState as ViewState.Loaded?)?.apply {
                showUpdateDialog = true
            } ?: ViewState.Loaded(listOf(), true))
        }
    }
}