package com.aperii

import android.app.Application
import android.os.Looper
import com.aperii.di.apiModule
import com.aperii.di.storeModule
import com.aperii.di.viewModelModule
import com.aperii.utilities.DimenUtils
import com.aperii.utilities.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.net.UnknownHostException
import kotlin.system.exitProcess

class Aperii : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Aperii)
            modules(
                apiModule(),
                storeModule(),
                viewModelModule()
            )
        }
        DimenUtils.density = resources.displayMetrics.density
        Logger.log("Application Initialized")
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler { thread, err ->
            if(thread == Looper.getMainLooper().thread) exitProcess(1)
            if(err is UnknownHostException) return@setDefaultUncaughtExceptionHandler
            Logger.default.error("Uncaught exception on thread ${thread.name}", err)
        }
    }
}