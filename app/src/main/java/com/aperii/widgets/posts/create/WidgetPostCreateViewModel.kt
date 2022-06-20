package com.aperii.widgets.posts.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.stores.StorePosts
import com.aperii.stores.StoreUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetPostCreateViewModel(
    state: SavedStateHandle,
    private val users: StoreUsers,
    private val posts: StorePosts
) : AppViewModel<WidgetPostCreateViewModel.ViewState>(ViewState()) {
    val me = users.me!!

    open class ViewState {
        data class ReplyLoaded(val replyTo: Post) : ViewState()
    }

    fun post(text: String, replyTo: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            posts.create(text, replyTo)
        }
    }

    init {
        state.get<String>(WidgetPostCreate.REPLY_TO)?.let {
            viewModelScope.launch(Dispatchers.IO) {
                posts.fetchPost(it)?.let { post -> updateViewState(ViewState.ReplyLoaded(post)) }
            }
        }
    }
}