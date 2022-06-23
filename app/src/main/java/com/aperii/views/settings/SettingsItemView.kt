package com.aperii.views.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.BuildConfig
import com.aperii.R
import com.aperii.databinding.SettingsListItemBinding
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen

class SettingsItemView(context: Context, private val attrs: AttributeSet): ConstraintLayout(context, attrs) {

    val binding: SettingsListItemBinding by viewBinding(CreateMethod.INFLATE)
    private var mDestination: Fragment? = null

    var label: CharSequence
        get() = binding.itemLabel.text
        set(value) {
            binding.itemLabel.text = value
        }

    var description: CharSequence
        get() = binding.itemDescription.text
        set(value) {
            binding.itemDescription.text = value
        }

    var icon: Drawable
        get() = binding.itemIcon.drawable
        set(value) = binding.itemIcon.setImageDrawable(value)

    var destination: Fragment?
        get() = mDestination
        set(value) {
            mDestination = value
            setOnClickListener {
                context.openScreen(
                    allowBack = true,
                    animation = ScreenManager.Animations.SLIDE_FROM_RIGHT,
                    screen = value
                )
            }
        }

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.SettingsItemView)) {

            getText(R.styleable.SettingsItemView_label)?.run {
                label = this
            }

            getText(R.styleable.SettingsItemView_description)?.run {
                description = this
            }

            getDrawable(R.styleable.SettingsItemView_icon)?.run {
                icon = this
            }

            getString(R.styleable.SettingsItemView_name)?.runCatching {
                val className = if(this.startsWith(".")) "${BuildConfig.APPLICATION_ID}$this" else this
                val fragment = Class.forName(className)
                if(fragment.newInstance() is Fragment) destination = fragment.newInstance() as Fragment
            }

        }
    }

}