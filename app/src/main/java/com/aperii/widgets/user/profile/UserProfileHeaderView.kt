package com.aperii.widgets.user.profile

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.databinding.UserProfileHeaderViewBinding
import com.aperii.utilities.text.AperiiRenderer
import com.aperii.utilities.text.nodes.BioRenderContext

class UserProfileHeaderView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    val binding = UserProfileHeaderViewBinding.bind(LayoutInflater.from(context).inflate(R.layout.user_profile_header_view, this, true))
    init {

    }

    private fun configureBanner(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        val banner = findViewById<ImageView>(R.id.banner)
        loaded.user.banner.run {
            if (isNotEmpty()) banner.load(this)
        }
    }

    private fun configureAvatar(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        val avatar = findViewById<ImageView>(R.id.avatar)
        loaded.user.avatar.run {
            avatar.load(if (isNotEmpty()) "https://api.aperii.com/cdn/avatars/${this}" else "https://aperii.com/av.png") {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.img_default_avatar)
            }
        }
    }

    private fun configureDisplayName(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        val displayName = findViewById<TextView>(R.id.display_name)
        displayName.apply {
            text = loaded.user.displayName
            if (loaded.user.flags.verified) setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_badge_20dp,
                0
            )
        }
    }

    private fun configureUsername(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        val username = findViewById<TextView>(R.id.username)
        username.text = context.getString(R.string.username, loaded.user.username)
    }

    private fun configureBio(loaded: UserProfileHeaderViewModel.ViewState.Loaded) = binding.bio.apply {
        visibility = if(loaded.user.bio.isNotEmpty()) VISIBLE else GONE
        text = AperiiRenderer.render(loaded.user.bio, BioRenderContext(context))
        movementMethod = LinkMovementMethod()
    }

    fun updateViewState(viewState: UserProfileHeaderViewModel.ViewState.Loaded) {
        configureBanner(viewState)
        configureAvatar(viewState)
        configureDisplayName(viewState)
        configureUsername(viewState)
        configureBio(viewState)
    }

}