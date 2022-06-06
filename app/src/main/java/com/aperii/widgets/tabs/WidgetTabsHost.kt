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
import com.aperii.stores.StoreNavigation
import com.aperii.utilities.Logger
import com.aperii.utilities.color.ColorUtils.getThemedColor
import org.koin.android.ext.android.inject

class WidgetTabsHost : AppFragment() {
    private var navTabToFragment = HashMap<NavigationTab, FragmentContainerView>()
    private var navTabToIcon = HashMap<NavigationTab, ImageView>()

    private lateinit var root: View
    private lateinit var tabs: TabsHostBottomNav

    val navStore: StoreNavigation by inject()

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
        navTabToFragment = hashMapOf(
            NavigationTab.HOME to root.findViewById(R.id.widget_tabs_host_home),
            NavigationTab.DISCOVER to root.findViewById(R.id.widget_tabs_host_discover),
            NavigationTab.INBOX to root.findViewById(R.id.widget_tabs_host_notifications),
            NavigationTab.PROFILE to root.findViewById(R.id.widget_tabs_host_profile)
        )

        navTabToIcon = hashMapOf(
            NavigationTab.HOME to tabs.findViewById(R.id.tabs_host_item_home_icon),
            NavigationTab.DISCOVER to tabs.findViewById(R.id.tabs_host_item_discover_icon),
            NavigationTab.INBOX to tabs.findViewById(R.id.tabs_host_item_notifications_icon),
            NavigationTab.PROFILE to tabs.findViewById(R.id.tabs_host_item_profile_icon),
        )

        updateNavIcons()
        navigateToTab(navStore.navigationTab)
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
        for (navTab in navTabToFragment) {
            navTab.value.visibility = if (navTab.key == tab) View.VISIBLE else View.GONE
            if(navTab.key == tab) {
                (childFragmentManager.findFragmentById(navTab.value.id) as TabbedFragment).onTabSelected(navStore.navigationTab == tab)
            }
        }
        if(navStore.navigationTab != tab) Logger("WidgetTabHost").verbose("Switched to tab ${tab.javaClass.simpleName}.${tab.name}")
        navStore.navigationTab = tab
    }

    private fun updateNavIcons() {
        for (navItem in navTabToIcon)
            navItem.value.drawable.setTint(
                if (navItem.key == navStore.navigationTab)
                    requireContext().getThemedColor(R.attr.colorPrimary)
                else
                    requireContext().getThemedColor(R.attr.iconOnBackground)
            )
    }

}