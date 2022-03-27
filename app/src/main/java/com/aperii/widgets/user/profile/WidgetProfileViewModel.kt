package com.aperii.widgets.user.profile

import androidx.lifecycle.SavedStateHandle
import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.models.user.CoreUser
import com.aperii.models.user.User
import com.aperii.stores.StoreShelves
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe

class WidgetProfileViewModel(state: SavedStateHandle) :
    AppViewModel<WidgetProfileViewModel.ViewState>(ViewState.Loading()) {
    val me = StoreShelves.users.me!!
    private var source = ""
        set(value) = ViewState.Loaded().run {
            RestAPI.INSTANCE.getUserPosts(value).observe {
                this@run.posts = this
                updateViewState(this@run)
            }
            RestAPI.INSTANCE.getUser(value).observe {
                this@run.user = CoreUser.fromApi(this)
                updateViewState(this@run)
            }
        }

    open class ViewState {
        class Loading : ViewState() {
            val posts: List<Post> = emptyList()
        }

        class Loaded : ViewState() {
            var posts: List<Post> = listOf()
            var user: User = RestAPI.EMPTY_USER
        }
    }

    init {
        updateViewState(ViewState.Loading())
        source = state.get<String>(WidgetProfile.EXTRA_USER) ?: "@me"
    }
}