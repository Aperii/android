package com.aperii.widgets.debugging.apps

import android.content.Context
import com.aperii.api.auth.LoginResult
import com.aperii.api.error.ErrorResponse
import com.aperii.models.user.MeUser
import com.aperii.rest.RestAPIParams
import com.aperii.stores.StoreUsers
import com.aperii.utilities.rest.AuthAPI
import com.aperii.utilities.rest.HttpUtils.fold
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.debugging.apps.base.BaseDebugApplication
import com.aperii.widgets.tabs.WidgetTabsHost
import com.google.gson.GsonBuilder
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.common.orElse
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountManager(private val context: Context) : BaseDebugApplication(), KoinComponent {

    private val api: RestAPI by inject()
    private val authApi: AuthAPI by inject()
    private val users: StoreUsers by inject()

    class LoginArgs(parser: ArgParser) {

        val creds by parser.putting("-c", "--credentials", "--username-and-password", help = "")
            .default(null)

        val token by parser.storing("-t", "--token", help = "").default(null)

    }

    override fun onExec(args: List<String>) {
        if (args.isEmpty()) return send("Missing subcommand")
        when (args[0]) {
            "login" -> login(args.drop(1).toTypedArray())
            "token" -> send(api.currentToken)
            "me" -> send(GsonBuilder().setPrettyPrinting().create().toJson(users.me))
        }
    }

    private fun login(args: Array<String>) {
        ArgParser(args).parseInto(::LoginArgs).run {
            if (creds.isNullOrEmpty() && !token.isNullOrBlank()) {
                val oldToken = api.currentToken
                api.currentToken = token as String
                runBlocking {
                    api.getMe().fold<ErrorResponse>(
                        onSuccess = {
                            users.me = MeUser.fromApi(it)
                            context.openScreen<WidgetTabsHost>(allowBack = false)
                        },
                        onError = {
                            api.currentToken = oldToken
                            send("Failed to log in with that token")
                        }
                    )
                }
            } else if (!creds.isNullOrEmpty()) {
                runBlocking {
                    val login = creds!!.entries.first()
                    authApi.login(
                        RestAPIParams.LoginBody(login.key, login.value)
                    ).fold<LoginResult, ErrorResponse>(
                        onSuccess = {
                            api.currentToken = it.token
                            api.getMe().ifSuccessful { me ->
                                users.me = MeUser.fromApi(me)
                                context.openScreen<WidgetTabsHost>(allowBack = false)
                            }
                        },
                        onError = {
                            send("Failed to login with those credentials")
                        }
                    )
                }
            }
        }
    }

}

fun ArgParser.putting(vararg names: String, help: String) =
    option<MutableMap<String, String>>(
        *names,
        argNames = listOf("KEY", "VALUE"),
        help = help
    ) {
        value.orElse { mutableMapOf() }.apply {
            put(arguments.first(), arguments.last())
        }
    }