package com.aperii.widgets.tabs

import com.aperii.app.AppFragment

abstract class TabbedFragment : AppFragment() {
    abstract fun onTabSelected(previouslySelected: Boolean)
}