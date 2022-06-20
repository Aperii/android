package com.aperii.widgets.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.stores.StorePosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetPostViewModel(state: SavedStateHandle, private val posts: StorePosts) :
    AppViewModel<WidgetPostViewModel.ViewState>(ViewState.Loading()) {
    var post: Post? = null
    var replies = emptyList<Post>()

    open class ViewState {
        class Loading : ViewState()
        data class Loaded(val post: Post?, val replies: List<Post>) : ViewState()
    }

    private suspend fun getPost(id: String) = posts.fetchPost(id)?.let {
        post = it
        updateViewState(ViewState.Loaded(post, replies))
    }

    private suspend fun getReplies(id: String) = posts.fetchPostReplies(id).let {
        replies = it
        updateViewState(ViewState.Loaded(post, replies))
    }

    init {
        state.get<String?>(WidgetPost.EXTRA_POST)?.run {
            viewModelScope.launch(Dispatchers.IO) {
                getPost(this@run)
                getReplies(this@run)
            }
        }
    }

}