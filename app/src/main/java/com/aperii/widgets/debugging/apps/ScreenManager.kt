package com.aperii.widgets.debugging.apps

import android.content.Context
import androidx.fragment.app.Fragment
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.debugging.apps.base.BaseDebugApplication
import com.aperii.widgets.debugging.apps.base.InvalidArgumentException
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.common.orElse

class ScreenManager(val context: Context) : BaseDebugApplication() {

    class Args(parser: ArgParser) {

        val allowBack by parser.flagging("-b", "--allow-back", help = "")

        val fish by parser.flagging("--fish", help = "")

        val screen by parser.storing("-c", "--class", help = "") {
            try {
                val c = Class.forName(this)
                if (c.newInstance() !is Fragment) throw InvalidArgumentException("Class needs to be an instance of a fragment")
                c.newInstance() as Fragment
            } catch (err: Throwable) {
                if(err is ReflectiveOperationException) throw InvalidArgumentException("Class not found") else throw err
            }
        }.default(null as Fragment?)

    }

    override fun onExec(args: List<String>) = program {
        when(args[0]) {
            "open" -> {
                ArgParser(args.drop(1).toTypedArray()).parseInto(::Args).run {
                    if(fish) return@program send("<><")
                    context.openScreen(
                        allowBack,
                        screen = screen
                    )
                }
            }
        }
    }

}