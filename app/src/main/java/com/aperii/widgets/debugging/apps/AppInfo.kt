package com.aperii.widgets.debugging.apps

import com.aperii.BuildConfig
import com.aperii.widgets.debugging.apps.base.BaseDebugApplication

class AppInfo: BaseDebugApplication() {

    override fun onExec(args: List<String>) = send("""
        Aperii v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
        
        Debuggable: ${BuildConfig.DEBUG}
        API Version: ${BuildConfig.BASE_URL.split("/")[3]}
    """.trimIndent())

}