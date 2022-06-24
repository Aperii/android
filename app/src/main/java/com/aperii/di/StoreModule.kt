package com.aperii.di

import com.aperii.stores.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun storeModule() = module {

    singleOf(::StoreAuth)
    singleOf(::StoreEmojis)
    singleOf(::StoreExperiments)
    singleOf(::StoreNavigation)
    singleOf(::StorePosts)
    singleOf(::StorePreferences)
    singleOf(::StoreUsers)

}