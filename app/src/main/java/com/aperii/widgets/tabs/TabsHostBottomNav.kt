package com.aperii.widgets.tabs

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.aperii.R
import com.aperii.utilities.settings.settings

class TabsHostBottomNav(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val experiments by settings("StoreExperiments")

    fun setOnHomeTabClicked(onClick: OnClickListener) {
        findViewById<View>(R.id.tabs_host_item_home).setOnClickListener(onClick)
    }

    fun setOnDiscoverTabClicked(onClick: OnClickListener) {
        findViewById<View>(R.id.tabs_host_item_discover).setOnClickListener(onClick)
    }

    fun setOnInboxTabClicked(onClick: OnClickListener) {
        findViewById<View>(R.id.tabs_host_item_notifications).setOnClickListener(onClick)
    }

    fun setOnProfileTabClicked(onClick: OnClickListener) {
        findViewById<View>(R.id.tabs_host_item_profile).setOnClickListener(onClick)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.tabs_host_bottom_nav, this, true)
        with(experiments["admin_tab_03_13_22", 0]) {
            if (this == 1) {
                findViewById<View>(R.id.tabs_host_item_admin).visibility = VISIBLE
                findViewById<LinearLayout>(R.id.tabs_host_bottom_nav_container).apply {
                    layoutParams = (layoutParams as LayoutParams).apply {
                        weightSum = 6.25f
                    }
                }
            }
        }
    }

}