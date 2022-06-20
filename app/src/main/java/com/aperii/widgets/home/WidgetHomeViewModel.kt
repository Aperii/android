package com.aperii.widgets.home

import androidx.lifecycle.viewModelScope
import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.utilities.Utils.runInThread
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.update.UpdateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetHomeViewModel(private val api: RestAPI) :
    AppViewModel<WidgetHomeViewModel.ViewState>() {
    var update: UpdateUtils.Release? = null

    open class ViewState {
        class Loading : ViewState()

        class Loaded(val posts: List<Post>, var showUpdateDialog: Boolean = false) : ViewState()
    }

    init {
        updateViewState(ViewState.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            api.getFeed().body()?.let {
                updateViewState(ViewState.Loaded(it))
            }
        }
        runInThread {
            val (hasUpdate, release) = UpdateUtils.checkUpdate()
            update = release
            if (hasUpdate) updateViewState((viewState as ViewState.Loaded?)?.apply {
                showUpdateDialog = true
            } ?: ViewState.Loaded(listOf(), true))
        }
    }
}