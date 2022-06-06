package com.aperii.di

import com.aperii.utilities.rest.AuthAPI
import com.aperii.utilities.rest.RestAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun apiModule() = module {

    singleOf(::RestAPI)
    singleOf(::AuthAPI)

}