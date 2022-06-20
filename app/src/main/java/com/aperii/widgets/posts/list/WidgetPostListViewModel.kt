package com.aperii.widgets.posts.list

import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.models.user.User
import com.aperii.stores.StorePosts
import com.aperii.stores.StoreUsers
import com.aperii.utilities.rest.RestAPI

class WidgetPostListViewModel(
    private val users: StoreUsers,
    private val api: RestAPI,
    private val posts: StorePosts
) : AppViewModel<WidgetPostListViewModel.ViewState>() {
    val me = users.me!!
    var isProfile = false

    open class ViewState {
        class Loading : ViewState()
        class Loaded : ViewState() {
            var posts: List<Post>? = null
            var user: User? = null
        }
    }

    suspend fun fetchMePosts() {
        api.getMePosts().body()?.let {
            ViewState.Loaded().apply {
                posts = it
                user = me
                updateViewState(this)
            }
        }
    }

    suspend fun fetchFeed() {
        api.getFeed().body()?.let {
            ViewState.Loaded().apply {
                posts = it
                updateViewState(this)
            }
        }
    }

    suspend fun fetchPost(id: String) {
        posts.fetchPost(id)?.run {
            ViewState.Loaded().apply {
                posts = listOf(this@run)
                updateViewState(this)
            }
        }
    }

    init {
        updateViewState(ViewState.Loading())
    }

}