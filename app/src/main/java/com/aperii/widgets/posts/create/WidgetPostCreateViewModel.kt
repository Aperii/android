package com.aperii.widgets.posts.create

import com.aperii.app.AppViewModel
import com.aperii.stores.StoreShelves

class WidgetPostCreateViewModel : AppViewModel<WidgetPostCreateViewModel.ViewState>() {
    val me = StoreShelves.users.me!!

    class ViewState

    init {
        updateViewState(ViewState())
    }
}