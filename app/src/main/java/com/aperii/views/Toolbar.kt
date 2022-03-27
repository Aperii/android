package com.aperii.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.ActionMenuView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.stores.StoreShelves
import com.aperii.utilities.DimenUtils.dp
import com.aperii.utilities.Utils.appActivity
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.auth.WidgetAuthLanding
import com.aperii.widgets.debugging.WidgetDebugging
import com.aperii.widgets.tabs.NavigationTab

class Toolbar(context: Context, private val attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    var title: CharSequence
        get() = findViewById<TextView>(R.id.toolbar_title).text
        set(value) = findViewById<TextView>(R.id.toolbar_title).run {
            text = value
        }

    var subtitle: CharSequence
        get() = findViewById<TextView>(R.id.toolbar_subtitle).text
        set(value) = findViewById<TextView>(R.id.toolbar_subtitle).run {
            visibility = View.VISIBLE
            text = value
        }

    var navigationIcon: Drawable
        get() = findViewById<ImageButton>(R.id.nav_button).drawable
        set(value) = findViewById<ImageButton>(R.id.nav_button).run {
            visibility = VISIBLE
            setImageDrawable(value)
        }

    var avatar: String = ""
        set(value) = findViewById<ImageView>(R.id.toolbar_avatar).run {
            load(value) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.img_default_avatar)
            }
        }

    var isDeveloper: Boolean = false

    fun setHomeAsUpAction(onClick: OnClickListener) =
        findViewById<ImageButton>(R.id.nav_button).setOnClickListener(onClick)

    fun setOnMenuItemPicked(onItemSelected: ActionMenuView.OnMenuItemClickListener) =
        findViewById<ActionMenuView>(R.id.more_options_menu).setOnMenuItemClickListener(
            onItemSelected
        )

    init {
        LayoutInflater.from(context).inflate(R.layout.toolbar, this)
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

        StoreShelves.users.me?.run {
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
        val dropdown = window.contentView

        dropdown.findViewById<View>(R.id.item_experiments).apply {
            if (isDeveloper) visibility = View.VISIBLE
        }

        dropdown.findViewById<View>(R.id.item_debug).apply {
            if (isDeveloper) visibility = View.VISIBLE
            setOnClickListener {
                appActivity.openScreen<WidgetDebugging>(
                    allowBack = true,
                    animation = ScreenManager.Animations.SCALE_CENTER
                )
                window.dismiss()
            }
        }

        dropdown.findViewById<View>(R.id.item_logout).setOnClickListener {
            appActivity.prefs.clear("APR_auth_tok")
            StoreShelves.navigation.navigationTab = NavigationTab.HOME
            appActivity.openScreen<WidgetAuthLanding>(
                allowBack = false,
                animation = ScreenManager.Animations.SCALE_CENTER
            )
            window.dismiss()
        }

    }
}