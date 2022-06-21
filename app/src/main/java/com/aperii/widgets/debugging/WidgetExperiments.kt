package com.aperii.widgets.debugging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetExperimentsBinding
import com.aperii.stores.Experiment
import com.aperii.utilities.rx.RxUtils.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

class WidgetExperiments : AppFragment() {

    private val binding: WidgetExperimentsBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: WidgetExperimentsViewModel by viewModel()

    inner class ExpAdapter(private val mData: List<Experiment>) :
        RecyclerView.Adapter<ExpAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(data: Experiment) {
                itemView.findViewById<TextView>(R.id.exp_name).text = data.name
                itemView.findViewById<TextView>(R.id.exp_id).text = data.id
                val bucketLabels = mutableListOf<String>()
                var desc = ""
                for (i in 0..data.buckets.lastIndex) {
                    bucketLabels.add("Bucket $i")
                    desc += "${data.buckets[i]}\n"
                }
                itemView.findViewById<Spinner>(R.id.exp_bucket).apply {
                    adapter = ArrayAdapter(
                        itemView.context,
                        android.R.layout.simple_spinner_dropdown_item,
                        bucketLabels
                    )
                    setSelection(data.bucket)
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            data.bucket = position
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
                itemView.findViewById<TextView>(R.id.exp_desc).text = desc.trimIndent()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.widget_item_experiment, parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(mData[position])

        override fun getItemCount(): Int = mData.size

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.observeViewState().observe(this::configureUI)
        return binding.root
    }

    private fun configureUI(viewState: WidgetExperimentsViewModel.ViewState) {
        when (viewState) {
            is WidgetExperimentsViewModel.ViewState.Loaded -> {
                configureToolbar()
                configureExperiments(viewState)
            }
        }
    }

    private fun configureExperiments(loaded: WidgetExperimentsViewModel.ViewState.Loaded) =
        binding.expList.apply {
            adapter = ExpAdapter(loaded.experiments)
            layoutManager = LinearLayoutManager(context)
        }

    private fun configureToolbar() = toolbar?.apply {
        hideAvatar()
        setHomeAsUpAction {
            appActivity.onBackPressed()
        }
    }

}