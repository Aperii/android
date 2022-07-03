package com.aperii.widgets.debugging

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetDebuggingBinding
import com.aperii.stores.StoreAuth
import com.aperii.stores.StoreUsers
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils.setClipboard
import com.aperii.utilities.Utils.showToast
import com.aperii.utilities.color.ColorUtils.getThemedColor
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.settings.settings
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetDebugging : AppFragment(), KoinComponent {

    val binding: WidgetDebuggingBinding by viewBinding(CreateMethod.INFLATE)
    lateinit var adp: Adapter
    val prefs by settings()
    private val users: StoreUsers by inject()
    private val api: RestAPI by inject()
    private val auth: StoreAuth by inject()
    private val viewModel: WidgetDebuggingViewModel by viewModel()

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
        viewModel.observeViewState().observe(::configureUI)
        viewModel.loadAppsWithContext(binding.root.context)
        return binding.root
    }

    private fun configureUI(viewState: WidgetDebuggingViewModel.ViewState) {
        when (viewState) {
            is WidgetDebuggingViewModel.ViewState.Loaded -> {
                configureToolbar(viewState)
                configureLogs(viewState)
                configureCommandInput()
            }
            is WidgetDebuggingViewModel.ViewState.AppLog -> {
                send(viewState.msg)
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
        viewModel.executeApp(command.split(Regex(" +")))
    }

    private fun send(message: String) {
        adp.addItem(Logger.LoggedItem(Logger.LoggedItem.Type.INFO, message))
        binding.logItems.scrollToPosition(adp.data.lastIndex)
    }
}