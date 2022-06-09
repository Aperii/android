package com.aperii.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.aperii.R
import com.aperii.databinding.UserInfoChipBinding

class UserInfoChip(context: Context): ConstraintLayout(context) {
    val binding: UserInfoChipBinding =
        UserInfoChipBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.user_info_chip, this)
        )

    var label: String
        get() = binding.chipText.text.toString()
        set(value) = binding.chipText.run {
            text = value
            visibility = VISIBLE
        }

    var icon: Drawable
        get() = binding.chipIcon.drawable
        set(value) = binding.chipIcon.setImageDrawable(value)

    var tint: Int
        get() = binding.chipText.currentTextColor
        set(@ColorInt value) = binding.run {
            chipIcon.drawable.setTint(value)
            chipText.setTextColor(value)
        }

    fun setIcon(@DrawableRes id: Int) = binding.chipIcon.setImageDrawable(ContextCompat.getDrawable(context, id))
}