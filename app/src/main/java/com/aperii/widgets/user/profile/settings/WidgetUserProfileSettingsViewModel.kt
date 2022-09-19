package com.aperii.widgets.user.profile.settings

import androidx.lifecycle.viewModelScope
import com.aperii.api.error.FieldError
import com.aperii.api.user.user.EditedProfile
import com.aperii.app.AppViewModel
import com.aperii.models.user.MeUser
import com.aperii.stores.StoreUsers
import com.aperii.utilities.rest.RestAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetUserProfileSettingsViewModel(private val users: StoreUsers, private val api: RestAPI) :
    AppViewModel<WidgetUserProfileSettingsViewModel.ViewState>(ViewState.Uninitialized()) {
    val me: MeUser?
        get() = users.me
    var changedDisplayName = null as String?
    var changedBio = null as String?
    var changedPronouns = null as String?

    open class ViewState {
        class Uninitialized : ViewState()
        data class Saved(val profile: MeUser, val errors: List<FieldError>?) : ViewState()
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            api.editProfile(changedDisplayName, changedBio, changedPronouns)
                .fold<EditedProfile>(
                    onSuccess = {
                        users.me = MeUser.fromApi(it.profile)
                        updateViewState(ViewState.Saved(MeUser.fromApi(it.profile), null))
                    },
                    onError = {
                        users.me = MeUser.fromApi(it.profile)
                        updateViewState(ViewState.Saved(MeUser.fromApi(it.profile), it.errors))
                    }
                )
        }
    }

}