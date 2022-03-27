package com.aperii.widgets.debugging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.utilities.screens.ScreenManager.openScreen

class WidgetFatalCrash : AppFragment(R.layout.widget_fatal_crash) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        appActivity.supportActionBar?.hide()
        view?.findViewById<View>(R.id.crash_action_restart)?.setOnClickListener {
            activity?.openScreen<WidgetFatalCrash>(allowBack = false)
        }
        return view
    }

}