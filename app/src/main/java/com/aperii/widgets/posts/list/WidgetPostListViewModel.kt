package com.aperii.widgets.posts.list

import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.models.user.User
import com.aperii.stores.StoreShelves
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe

class WidgetPostListViewModel : AppViewModel<WidgetPostListViewModel.ViewState>() {
    val me = StoreShelves.users.me!!
    var isProfile = false

    open class ViewState {
        class Loading : ViewState()
        class Loaded : ViewState() {
            var posts: List<Post>? = null
            var user: User? = null
        }
    }

    fun fetchMePosts() {
        RestAPI.INSTANCE.getMePosts().observe {
            ViewState.Loaded().apply {
                posts = this@observe
                user = me
                posts?.forEach {
                    StoreShelves.posts.updatePost(it)
                }
                updateViewState(this)
            }
        }
    }

    fun fetchFeed() {
        RestAPI.INSTANCE.getFeed().observe {
            ViewState.Loaded().apply {
                posts = this@observe
                updateViewState(this)
            }
        }
    }

    fun fetchPost(id: String) {
        StoreShelves.posts.fetchPost(id)?.run {
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