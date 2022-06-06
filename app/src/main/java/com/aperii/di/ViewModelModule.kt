package com.aperii.di

import com.aperii.app.MainViewModel
import com.aperii.widgets.auth.WidgetAuthLoginViewModel
import com.aperii.widgets.home.WidgetHomeViewModel
import com.aperii.widgets.posts.WidgetPostViewModel
import com.aperii.widgets.posts.create.WidgetPostCreateViewModel
import com.aperii.widgets.posts.list.WidgetPostListViewModel
import com.aperii.widgets.user.profile.WidgetProfileViewModel
import com.aperii.widgets.user.profile.settings.WidgetUserProfileSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

fun viewModelModule() = module {

    viewModelOf(::MainViewModel)

    viewModelOf(::WidgetAuthLoginViewModel)

    viewModelOf(::WidgetHomeViewModel)

    viewModelOf(::WidgetPostViewModel)
    viewModelOf(::WidgetPostCreateViewModel)
    viewModelOf(::WidgetPostListViewModel)

    viewModelOf(::WidgetProfileViewModel)
    viewModelOf(::WidgetUserProfileSettingsViewModel)

}