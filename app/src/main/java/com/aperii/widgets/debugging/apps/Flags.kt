package com.aperii.widgets.debugging.apps

import com.aperii.stores.StorePreferences
import com.aperii.widgets.debugging.apps.base.BaseDebugApplication
import com.xenomachina.argparser.ArgParser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Flags : BaseDebugApplication(), KoinComponent {

    private val preferences: StorePreferences by inject()

    class Args(parser: ArgParser) {
        private val flagRgx = "^([\\d\\D]+)=([\\d\\D]+)$".toRegex()

        val flags: List<Pair<String, String>> by parser.adding(
            "-s", "--set", help = "set a flag"
        ) {
            if (flagRgx.matches(this)) {
                val parsed = flagRgx.matchEntire(this)
                parsed?.groups?.get(1)!!.value to parsed.groups[2]!!.value
            } else {
                "" to ""
            }
        }

    }

    override fun onExec(args: List<String>) {
        kotlin.runCatching {
            ArgParser(args.toTypedArray()).parseInto(::Args).runCatching {
                for ((flag, value) in flags) {
                    when (flag.lowercase()) {
                        "experiments.enabled" -> {
                            value.toBooleanStrict().runCatching {
                                send("Flag $flag set to $this")
                                preferences.experimentsEnabled = this
                            }
                        }
                        else -> {
                            if (flag.isNotBlank()) send("Flag $flag set to $value")
                        }
                    }
                }
            }
        }
    }

}