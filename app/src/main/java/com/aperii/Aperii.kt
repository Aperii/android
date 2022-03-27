package com.aperii

import android.app.Application
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils.appContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Aperii : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Aperii)
        }
        appContext = this
        Logger.log("Application Initialized")
    }

}