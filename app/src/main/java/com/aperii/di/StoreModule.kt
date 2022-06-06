package com.aperii.di

import com.aperii.stores.StoreAuth
import com.aperii.stores.StoreNavigation
import com.aperii.stores.StorePosts
import com.aperii.stores.StoreUsers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun storeModule() = module {

    singleOf(::StoreAuth)
    singleOf(::StoreNavigation)
    singleOf(::StorePosts)
    singleOf(::StoreUsers)

}