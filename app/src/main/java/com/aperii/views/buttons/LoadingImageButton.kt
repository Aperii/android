package com.aperii.views.buttons

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.aperii.R
import com.aperii.databinding.LoadingImageButtonBinding

class LoadingImageButton : ConstraintLayout {

    var binding: LoadingImageButtonBinding =
        LoadingImageButtonBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.loading_image_button, this)
        )

    var icon: Drawable = binding.button.drawable
        set(value) = binding.button.run { setImageDrawable(value) }

    var isLoading: Boolean
        get() = binding.indicator.visibility == View.VISIBLE
        set(value) = binding.indicator.run {
            visibility = if (value) VISIBLE else GONE
            binding.button.visibility = if (!value) VISIBLE else GONE
        }

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet, @StyleRes styleInt: Int) : super(
        ctx,
        attrs,
        styleInt
    )

    override fun setOnClickListener(l: OnClickListener?) = binding.button.setOnClickListener(l)

}