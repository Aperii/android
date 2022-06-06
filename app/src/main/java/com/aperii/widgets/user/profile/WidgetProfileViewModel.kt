package com.aperii.widgets.user.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aperii.api.post.Post
import com.aperii.app.AppViewModel
import com.aperii.models.user.User
import com.aperii.stores.StoreUsers
import com.aperii.utilities.rest.RestAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetProfileViewModel(state: SavedStateHandle, private val users: StoreUsers, private val api: RestAPI) :
    AppViewModel<WidgetProfileViewModel.ViewState>(ViewState.Loading()) {
    val me = users.me
    private var source = ""
        set(value) = ViewState.Loaded().run {
            viewModelScope.launch(Dispatchers.IO) {
                api.getUserPosts(value).body()?.let {
                    this@run.posts = it
                    updateViewState(this@run)
                }

                users.fetchUser(value)?.let {
                    this@run.user = it
                    updateViewState(this@run)
                }
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