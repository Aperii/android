package com.aperii.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.widget.ActionMenuView
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.BuildConfig
import com.aperii.R
import com.aperii.databinding.DropdownBinding
import com.aperii.databinding.ToolbarBinding
import com.aperii.stores.StoreExperiments
import com.aperii.stores.StoreNavigation
import com.aperii.stores.StorePreferences
import com.aperii.stores.StoreUsers
import com.aperii.utilities.DimenUtils.dp
import com.aperii.utilities.Utils.openUrl
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.settings.settings
import com.aperii.widgets.auth.WidgetAuthLanding
import com.aperii.widgets.debugging.WidgetDebugging
import com.aperii.widgets.debugging.WidgetExperiments
import com.aperii.widgets.settings.WidgetSettings
import com.aperii.widgets.tabs.NavigationTab
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Toolbar(context: Context, private val attrs: AttributeSet) :
    ConstraintLayout(context, attrs), KoinComponent {

    private val binding: ToolbarBinding by viewBinding(CreateMethod.INFLATE)

    private val prefs by settings()
    private val users: StoreUsers by inject()
    private val nav: StoreNavigation by inject()
    private val experiments: StoreExperiments by inject()
    private val settings: StorePreferences by inject()

    var title: CharSequence
        get() = binding.toolbarTitle.text
        set(value) = binding.toolbarTitle.run {
            text = value
        }

    var subtitle: CharSequence
        get() = binding.toolbarSubtitle.text
        set(value) = binding.toolbarSubtitle.run {
            visibility = View.VISIBLE
            text = value
        }

    var navigationIcon: Drawable
        get() = binding.navButton.drawable
        set(value) = binding.navButton.run {
            visibility = VISIBLE
            setImageDrawable(value)
        }

    var avatar: String = ""
        set(value) = binding.toolbarAvatar.run {
            load(value) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.img_default_avatar)
            }
        }

    var isDeveloper: Boolean = false

    fun setHomeAsUpAction(onClick: OnClickListener) =
        binding.navButton.setOnClickListener(onClick)

    fun setOnMenuItemPicked(onItemSelected: ActionMenuView.OnMenuItemClickListener) =
        binding.moreOptionsMenu.setOnMenuItemClickListener(
            onItemSelected
        )

    init {
        val args: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)

        args.getText(R.styleable.Toolbar_title)?.run {
            title = this
        }

        args.getText(R.styleable.Toolbar_subtitle)?.run {
            subtitle = this
        }

        args.getDrawable(R.styleable.Toolbar_navigationIcon)?.run {
            navigationIcon = this
        }

        args.getResourceId(R.styleable.Toolbar_menu, 0).run {
            if (this != 0)
                MenuInflater(context)
                    .inflate(this, findViewById<ActionMenuView>(R.id.more_options_menu).menu)
        }

        findViewById<View>(R.id.toolbar_avatar).setOnClickListener {
            showPopup()
        }

        users.me?.run {
            this@Toolbar.avatar =
                if (avatar.isNotEmpty()) "https://api.aperii.com/cdn/avatars/${avatar}" else "https://aperii.com/av.png"
            isDeveloper = flags.staff
        }

        args.recycle()
    }

    fun showBadge(yes: Boolean = true) = findViewById<View>(R.id.toolbar_badge).run {
        visibility = if (yes) View.VISIBLE else View.GONE
    }

    fun hideAvatar() = findViewById<ImageView>(R.id.toolbar_avatar).run {
        visibility = View.GONE
    }

    private fun showPopup() {
        val popup = LayoutInflater.from(context).inflate(R.layout.dropdown, null)

        PopupWindow(
            popup,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).run {
            isOutsideTouchable = true
            configureDropdownItems(this)
            setBackgroundDrawable(context.getDrawable(R.drawable.dropdown_bg))
            elevation = 4.dp.toFloat()
            showAsDropDown(
                this@Toolbar.findViewById(R.id.toolbar_avatar),
                15.dp,
                15.dp,
                Gravity.RIGHT
            )
        }
    }

    private fun configureDropdownItems(window: PopupWindow) {
        val dropdown = DropdownBinding.bind(window.contentView)

        dropdown.itemBugReport.setOnClickListener {
            it.context.openUrl("https://github.com/Aperii/android/issues/new")
        }

        dropdown.itemDiscord.setOnClickListener {
            it.context.openUrl("https://discord.gg/Mryxr7zVtc")
        }

        dropdown.itemSettings.apply {
            visibility = if(experiments["settings_page_06_21_22"].bucket == 1) View.VISIBLE else View.GONE
            setOnClickListener {
                it.context.openScreen<WidgetSettings>()
                window.dismiss()
            }
        }

        dropdown.itemExperiments.apply {
            if (settings.experimentsEnabled) visibility = View.VISIBLE
            setOnClickListener {
                it.context.openScreen<WidgetExperiments>()
                window.dismiss()
            }
        }

        dropdown.itemDebug.apply {
            if (BuildConfig.DEBUG || isDeveloper) visibility = View.VISIBLE
            setOnClickListener {
                it.context.openScreen<WidgetDebugging>()
                window.dismiss()
            }
        }

        dropdown.itemLogout.setOnClickListener {
            prefs.clear("APR_auth_tok")
            nav.navigationTab = NavigationTab.HOME
            it.context.openScreen<WidgetAuthLanding>(
                allowBack = false
            )
            window.dismiss()
        }

    }
}