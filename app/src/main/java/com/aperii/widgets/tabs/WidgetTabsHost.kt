package com.aperii.widgets.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.aperii.R
import com.aperii.app.AppFragment
import com.aperii.stores.StoreShelves
import com.aperii.utilities.Logger
import com.aperii.utilities.color.ColorUtils.getThemedColor
import kotlin.collections.set

class WidgetTabsHost : AppFragment() {
    private val navTabToFragment = HashMap<NavigationTab, FragmentContainerView>()
    private val navTabToIcon = HashMap<NavigationTab, ImageView>()
    private var currentTab: NavigationTab = NavigationTab.HOME

    private lateinit var root: View
    private lateinit var tabs: TabsHostBottomNav

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.widget_tabs_host, container, false).run {
        root = this
        tabs = findViewById(R.id.widget_tabs_host_bottom_nav_view)
        configureUI()
        return this
    }

    private fun configureUI() {
        (activity as AppCompatActivity).supportActionBar?.show()
        configureTabs()
        configureTabListeners()
    }

    private fun configureTabs() {
        navTabToFragment.run {
            this[NavigationTab.HOME] = root.findViewById(R.id.widget_tabs_host_home)
            this[NavigationTab.DISCOVER] = root.findViewById(R.id.widget_tabs_host_discover)
            this[NavigationTab.INBOX] =
                root.findViewById(R.id.widget_tabs_host_notifications)
            this[NavigationTab.PROFILE] = root.findViewById(R.id.widget_tabs_host_profile)
        }

        navTabToIcon.run {
            this[NavigationTab.HOME] = tabs.findViewById(R.id.tabs_host_item_home_icon)
            this[NavigationTab.DISCOVER] = tabs.findViewById(R.id.tabs_host_item_discover_icon)
            this[NavigationTab.INBOX] =
                tabs.findViewById(R.id.tabs_host_item_notifications_icon)
            this[NavigationTab.PROFILE] = tabs.findViewById(R.id.tabs_host_item_profile_icon)
        }

        updateNavIcons()
        navigateToTab(StoreShelves.navigation.navigationTab)
    }

    private fun configureTabListeners() = tabs.apply {
        setOnHomeTabClicked {
            navigateToTab(NavigationTab.HOME)
            updateNavIcons()
        }

        setOnInboxTabClicked {
            navigateToTab(NavigationTab.INBOX)
            updateNavIcons()
        }

        setOnDiscoverTabClicked {
            navigateToTab(NavigationTab.DISCOVER)
            updateNavIcons()
        }

        setOnProfileTabClicked {
            navigateToTab(NavigationTab.PROFILE)
            updateNavIcons()
        }
    }

    private fun navigateToTab(tab: NavigationTab) {
        for (navTab in navTabToFragment)
            navTab.value.visibility = if (navTab.key == tab) View.VISIBLE else View.GONE
        if(StoreShelves.navigation.navigationTab != tab) Logger("WidgetTabHost").verbose("Switched to tab ${tab.javaClass.simpleName}.${tab.name}")
        StoreShelves.navigation.navigationTab = tab
    }

    private fun updateNavIcons() {
        for (navItem in navTabToIcon)
            navItem.value.drawable.setTint(
                if (navItem.key == StoreShelves.navigation.navigationTab)
                    requireContext().getThemedColor(R.attr.colorPrimary)
                else
                    requireContext().getThemedColor(R.attr.iconOnBackground)
            )
    }

}