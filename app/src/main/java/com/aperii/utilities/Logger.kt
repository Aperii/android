package com.aperii.utilities

import android.util.Log
import com.google.gson.GsonBuilder

class Logger(private val tag: String) {

    fun verbose(message: String) = log(LoggedItem.Type.VERBOSE, message)
    fun debug(message: String) = log(LoggedItem.Type.DEBUG, message)
    fun info(message: String) = log(LoggedItem.Type.INFO, message)
    fun warn(message: String) = log(LoggedItem.Type.WARN, message)
    fun error(message: String, th: Throwable? = null) = log(LoggedItem.Type.ERROR, message, th)

    fun logObject(type: LoggedItem.Type, obj: Any?) =
        log(type, GsonBuilder().setPrettyPrinting().create().toJson(obj))

    private fun log(type: LoggedItem.Type, message: String, th: Throwable? = null) {
        when (type) {
            LoggedItem.Type.VERBOSE -> Log.v(APP_NAME, "[$tag] $message")
            LoggedItem.Type.DEBUG -> Log.d(APP_NAME, "[$tag] $message")
            LoggedItem.Type.INFO -> Log.i(APP_NAME, "[$tag] $message")
            LoggedItem.Type.WARN -> Log.w(APP_NAME, "[$tag] $message")
            LoggedItem.Type.ERROR -> Log.e(APP_NAME, "[$tag] $message", th)
        }
        logs.add(
            LoggedItem(
                type,
                "[$tag] $message${
                    if (th != null) "\n${
                        th.stackTraceToString().trimIndent()
                    }" else ""
                }"
            )
        )
    }

    companion object {
        const val APP_NAME = "Aperii"
        val logs: MutableList<LoggedItem> = mutableListOf()
        val default = Logger(APP_NAME)
        fun log(message: String) = default.info(message)
    }

    data class LoggedItem(val type: Type, val message: String) {
        enum class Type {
            INFO,
            WARN,
            DEBUG,
            ERROR,
            VERBOSE
        }
    }

}