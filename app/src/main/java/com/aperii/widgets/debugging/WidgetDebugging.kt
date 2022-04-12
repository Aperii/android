package com.aperii.widgets.debugging

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aperii.BuildConfig
import com.aperii.R
import com.aperii.app.AppActivity
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetDebuggingBinding
import com.aperii.models.user.MeUser
import com.aperii.rest.RestAPIParams
import com.aperii.stores.StoreShelves
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils.setClipboard
import com.aperii.utilities.Utils.showToast
import com.aperii.utilities.color.ColorUtils.getThemedColor
import com.aperii.utilities.rest.AuthAPI
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.update.UpdateUtils
import com.aperii.widgets.auth.WidgetAuthLanding
import com.aperii.widgets.tabs.WidgetTabsHost
import com.aperii.widgets.updater.WidgetUpdater
import com.aperii.widgets.updater.WidgetUpdater.Companion.EXTRA_RELEASE
import com.google.gson.GsonBuilder

class WidgetDebugging : AppFragment() {

    lateinit var binding: WidgetDebuggingBinding
    lateinit var adp: Adapter

    inner class Adapter(var data: MutableList<Logger.LoggedItem>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(position: Int) {
                itemView.setOnLongClickListener {
                    it.context.setClipboard(data[position].message)
                    it.context.showToast("Copied to clipboard")
                    true
                }
                view.findViewById<TextView?>(R.id.log_message)?.apply {
                    text = data[position].message
                    setTextColor(
                        when (data[position].type) {
                            Logger.LoggedItem.Type.INFO -> context.getThemedColor(R.attr.textOnBackground)
                            Logger.LoggedItem.Type.WARN -> Color.YELLOW
                            Logger.LoggedItem.Type.DEBUG -> context.getThemedColor(R.attr.colorSuccess)
                            Logger.LoggedItem.Type.ERROR -> Color.RED
                            Logger.LoggedItem.Type.VERBOSE -> context.getThemedColor(R.attr.colorSecondary)
                        }
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        override fun getItemCount(): Int = data.size

        @SuppressLint("NotifyDataSetChanged")
        fun setItems(mData: MutableList<Logger.LoggedItem>) {
            data = mData
            notifyDataSetChanged()
        }

        fun addItem(item: Logger.LoggedItem) {
            data.add(item)
            notifyItemInserted(data.lastIndex)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.widget_debugging, container, false)
        binding = WidgetDebuggingBinding.bind(view)
        ViewModelProvider(this)[WidgetDebuggingViewModel::class.java].observeViewState()
            .observe(this::configureUI)
        return view
    }

    private fun configureUI(viewState: WidgetDebuggingViewModel.ViewState) {

        when (viewState) {
            is WidgetDebuggingViewModel.ViewState.Loaded -> {
                configureToolbar(viewState)
                configureLogs(viewState)
                configureCommandInput()
            }
        }
    }

    private fun configureToolbar(viewState: WidgetDebuggingViewModel.ViewState.Loaded) =
        toolbar?.run {
            hideAvatar()
            setHomeAsUpAction {
                appActivity.onBackPressed()
            }
            setOnMenuItemPicked {
                when (it.itemId) {
                    R.id.filter_logs -> {
                        it.isChecked = !it.isChecked
                        if (it.isChecked) adp.setItems(viewState.logs.filterNot { log ->
                            log.type == Logger.LoggedItem.Type.VERBOSE
                        } as MutableList<Logger.LoggedItem>) else adp.setItems(viewState.logs)

                    }
                }
                true
            }
        }

    private fun configureLogs(viewState: WidgetDebuggingViewModel.ViewState.Loaded) {
        adp = Adapter(mutableListOf())
        binding.logItems.apply {
            this.adapter = adp
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
        if (viewState.logs.size > 0)
            for (log in viewState.logs) {
                if (log.type != Logger.LoggedItem.Type.VERBOSE) adp.addItem(log)
                binding.logItems.scrollToPosition(viewState.logs.lastIndex)
            }
    }

    private fun configureCommandInput() {
        binding.commandInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                runCommand(binding.commandInput.text.toString())
                binding.commandInput.setText("")
                return@setOnEditorActionListener true
            }
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun runCommand(command: String) {
        send("> $command")
        val args = command.split(Regex(" +"))
        val parsedArgs =
            if (args.size > 1) CommandArgs.fromList(args.subList(1, args.size)) else CommandArgs()
        when (args.firstOrNull()) {
            "sm" -> sm(parsedArgs)
            "am" -> am(parsedArgs)
            "ai" -> ai()
            "echo" -> send(args.drop(1).joinToString(" "))
            "dl" -> Bundle().run {
                putSerializable(EXTRA_RELEASE, UpdateUtils.Release(1020, "v1.20 - Stable", listOf(
                    UpdateUtils.Asset(9266387L))))
                openScreen<WidgetUpdater>(binding.root.context, true, ScreenManager.Animations.SLIDE_FROM_RIGHT, this)
            }
            "clear" -> {
                adp.data.clear()
                adp.notifyDataSetChanged()
                Logger.logs.clear()
            }
            else -> help()
        }
    }

    class CommandArgs {
        private val mItems = HashMap<String, String>()
        var subcommand = ""

        operator fun get(option: String): String? = mItems[option]
        operator fun set(option: String, value: String) = mItems.set(option, value)

        fun has(option: String) = mItems[option] != null

        companion object {
            fun fromList(args: List<String>): CommandArgs {
                val res = CommandArgs()
                res.subcommand = args.elementAtOrNull(0) ?: ""
                for (arg in args) {
                    if (arg == res.subcommand) continue
                    if (arg.startsWith("-")) {
                        val nextArg = args.elementAtOrNull(args.indexOf(arg) + 1)
                        res[arg.replace(Regex("^-"), "")] =
                            if (nextArg == null || nextArg.startsWith("-")) "" else nextArg
                    }
                }
                return res
            }
        }

    }

    private fun send(message: String) {
        adp.addItem(Logger.LoggedItem(Logger.LoggedItem.Type.INFO, message))
        binding.logItems.scrollToPosition(adp.data.lastIndex)
    }

    private fun help() = send(
        """
            ======= Aperii Debug Tool (v1.0.1) =======
            
            sm open 
                [-c/--full-class className] <b/--allow-back> - Open any screen
                
            am
               token - Sends your token
               me - Sends your account information
               logout - Logs out
               login
                [-t/--token] Login with an account token
                [-u/--username] [-p/--pass/--password] Login with your own information
                
            clear - Clear logs
            
            ai - Get app information
    """.trimIndent()
    )

    private fun sm(args: CommandArgs) {
        val allowBack = args.has("b") || args.has("-allow-back")
        val className = if (args["c"].isNullOrBlank()) args["-full-class"] else args["c"]
        val fish = args.has("-fish")

        when (args.subcommand.lowercase()) {
            "open" -> {
                if (fish) return send("<><")
                if (className.isNullOrBlank()) return send("Class for screen not provided")
                try {
                    val c = Class.forName(className)
                    if (c.newInstance() !is Fragment) return send("Class has to be a fragment")
                    appActivity.openScreen(c.newInstance() as Fragment, allowBack)
                } catch (err: ReflectiveOperationException) {
                    return send("Class not found")
                }
            }
            else -> return send("Subcommand not found")
        }

    }

    private fun am(args: CommandArgs) {
        val username = args["u"] ?: args["-username"]
        val password = args["p"] ?: args["-pass"] ?: args["-password"]
        val token = args["t"] ?: args["-token"]
        when (args.subcommand.lowercase()) {
            "token" -> send(AppActivity.prefs["APR_auth_tok", ""])
            "me" -> send(GsonBuilder().setPrettyPrinting().create().toJson(StoreShelves.users.me))
            "logout" -> {
                AppActivity.prefs.clear("APR_auth_tok")
                appActivity.openScreen<WidgetAuthLanding>(
                    allowBack = false,
                    animation = ScreenManager.Animations.SCALE_CENTER
                )
            }
            "login" -> {
                if (!(username.isNullOrBlank() || password.isNullOrBlank())) AuthAPI.getInstance()
                    .login(RestAPIParams.LoginBody(username, password))
                    .observeAndCatch({
                        AppActivity.prefs["APR_auth_tok"] = token
                        RestAPI.getInstance(this.token).getMe().observe {
                            StoreShelves.users.me = MeUser.fromApi(this)
                            appActivity.openScreen<WidgetTabsHost>(
                                allowBack = false,
                                animation = ScreenManager.Animations.SCALE_CENTER
                            )
                        }
                    }, {
                        send("Error logging in")
                    }) else {
                    if (token.isNullOrBlank()) return send("No login information provided")
                    RestAPI.getInstance(token).getMe().observeAndCatch({
                        StoreShelves.users.me = MeUser.fromApi(this)
                        AppActivity.prefs["APR_auth_tok"] = token
                        appActivity.openScreen<WidgetTabsHost>(
                            allowBack = false,
                            animation = ScreenManager.Animations.SCALE_CENTER
                        )
                    }) { send("Invalid token") }
                }
            }
            else -> return send("Subcommand not found")
        }
    }

    private fun ai() = send(
        """
        Aperii v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
        
        Debuggable: ${BuildConfig.DEBUG}
        API Version: ${BuildConfig.BASE_URL.split("/")[3]}
    """.trimIndent()
    )
}