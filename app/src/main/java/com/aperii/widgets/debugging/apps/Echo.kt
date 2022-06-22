package com.aperii.widgets.debugging.apps

import com.aperii.widgets.debugging.apps.base.BaseDebugApplication

class Echo: BaseDebugApplication() {

    override fun onExec(args: List<String>) {
        send(args.joinToString(" "))
    }

}