package com.aperii.widgets.user.profile

import com.aperii.app.AppViewModel
import com.aperii.models.user.User

class UserProfileHeaderViewModel : AppViewModel<UserProfileHeaderViewModel.ViewState>() {

    open class ViewState {
        class Loaded(val user: User)
    }

}