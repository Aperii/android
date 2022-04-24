package com.aperii.widgets.user.profile.settings

import com.aperii.api.error.FieldError
import com.aperii.api.user.user.EditedProfile
import com.aperii.app.AppViewModel
import com.aperii.models.user.MeUser
import com.aperii.stores.StoreShelves
import com.aperii.utilities.rest.HttpUtils.body
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.google.gson.Gson
import retrofit2.HttpException

class WidgetUserProfileSettingsViewModel: AppViewModel<WidgetUserProfileSettingsViewModel.ViewState>(ViewState.Uninitialized()) {
    var me: MeUser? = null
        get() = StoreShelves.users.me
    var changedDisplayName = null as String?
    var changedBio = null as String?
    var changedPronouns = null as String?

    open class ViewState {
        class Uninitialized : ViewState()
        data class Saved(val profile: MeUser, val errors: List<FieldError>?) : ViewState()
    }

    fun save() {
        RestAPI.INSTANCE.editProfile(changedDisplayName, changedBio, changedPronouns).observeAndCatch( {
            StoreShelves.users.me = MeUser.fromApi(profile)
            updateViewState(ViewState.Saved(MeUser.fromApi(profile), null))
        }) {
            when(this) {
                is HttpException -> body<EditedProfile>()?.let {
                    StoreShelves.users.me = MeUser.fromApi(it.profile)
                    updateViewState(ViewState.Saved(MeUser.fromApi(it.profile), it.errors))
                }
            }
        }
    }

}