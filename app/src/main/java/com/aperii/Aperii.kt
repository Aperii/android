package com.aperii

import android.app.Application
import com.aperii.utilities.DimenUtils
import com.aperii.utilities.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Aperii : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Aperii)
        }
        DimenUtils.density = resources.displayMetrics.density
        Logger.log("Application Initialized")
    }

}