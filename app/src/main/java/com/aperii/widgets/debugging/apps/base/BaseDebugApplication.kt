package com.aperii.widgets.debugging.apps.base

import com.aperii.utilities.Logger
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseDebugApplication {

    val stdout: Observable<String> = BehaviorSubject.create()

    abstract fun onExec(args: List<String>)

    fun send(msg: String) = (stdout as BehaviorSubject).onNext(msg)

    protected fun program(block: () -> Unit) {
        try {
            block()
        } catch (err: Throwable) {
            when (err) {
                is InvalidArgumentException -> send(err.message)
                else -> {
                    send("Error running command")
                    Logger.default.error("Error running command", err)
                }
            }
        }
    }

}

class InvalidArgumentException(override val message: String) : Error()