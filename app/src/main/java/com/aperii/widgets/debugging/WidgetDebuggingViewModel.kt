package com.aperii.widgets.debugging

import android.content.Context
import com.aperii.app.AppViewModel
import com.aperii.stores.StorePreferences
import com.aperii.utilities.Logger
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.widgets.debugging.apps.*
import com.aperii.widgets.debugging.apps.base.BaseDebugApplication

class WidgetDebuggingViewModel(private val prefs: StorePreferences) :
    AppViewModel<WidgetDebuggingViewModel.ViewState>() {

    private val applications: MutableMap<String, BaseDebugApplication> = mutableMapOf(
        "flags" to Flags(),
        "ping" to Ping(),
        "ai" to AppInfo(),
        "echo" to Echo()
    )

    open class ViewState {
        class Loaded(val logs: MutableList<Logger.LoggedItem>) : ViewState()
        class AppLog(val msg: String) : ViewState()
    }

    fun loadAppsWithContext(context: Context) {
        applications["sm"] = ScreenManager(context)
        applications["sm"]?.stdout?.observe {
            updateViewState(ViewState.AppLog(this))
        }

        applications["am"] = AccountManager(context)
        applications["am"]?.stdout?.observe {
            updateViewState(ViewState.AppLog(this))
        }
    }

    fun executeApp(args: List<String>) {
        applications[args.first()]?.onExec(args.subList(1, args.size))
    }

    private fun listenToStdOut() {
        for ((_, app) in applications) {
            app.stdout.observe {
                updateViewState(ViewState.AppLog(this))
            }
        }
    }

    init {
        updateViewState(ViewState.Loaded(Logger.logs))
        listenToStdOut()
    }
}