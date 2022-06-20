package com.aperii.views.buttons

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.databinding.LoadingImageButtonBinding

class LoadingImageButton : ConstraintLayout {

    val binding: LoadingImageButtonBinding by viewBinding(CreateMethod.INFLATE)

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