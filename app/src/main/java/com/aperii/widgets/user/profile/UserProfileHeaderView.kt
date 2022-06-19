package com.aperii.widgets.user.profile

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.databinding.UserProfileHeaderViewBinding
import com.aperii.stores.StoreUsers
import com.aperii.utilities.text.Renderer
import com.aperii.utilities.text.nodes.BioRenderContext
import com.aperii.utilities.time.TimeUtils
import com.aperii.views.UserInfoChip
import com.aperii.widgets.user.profile.settings.WidgetUserProfileSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserProfileHeaderView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet), KoinComponent {
    val binding: UserProfileHeaderViewBinding by viewBinding(CreateMethod.INFLATE)

    val users: StoreUsers by inject()

    private fun configureBanner(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        val banner = findViewById<ImageView>(R.id.banner)
        loaded.user.banner.run {
            if (isNotEmpty()) banner.load(this)
        }
    }

    private fun configureActionButtons(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        binding.btnEditProfile.run {
            visibility = if(loaded.user.id == users.me?.id && !loaded.edit) VISIBLE else GONE
            setOnClickListener {
                WidgetUserProfileSettings.open(it.context)
            }
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
        binding.displayName.apply {
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
        binding.username.text = context.getString(R.string.username, loaded.user.username)
    }

    private fun configureBio(loaded: UserProfileHeaderViewModel.ViewState.Loaded) = binding.bio.apply {
        visibility = if(loaded.user.bio.isNotEmpty()) VISIBLE else GONE
        text = Renderer.render(loaded.user.bio, BioRenderContext(context))
        if(!loaded.edit) movementMethod = LinkMovementMethod()
    }

    private fun configureUserDetails(loaded: UserProfileHeaderViewModel.ViewState.Loaded) {
        val user = loaded.user
        with(binding.userInfo) {
            removeAllViews()
            if(user.flags.staff || user.flags.admin) addView(UserInfoChip(context).apply { setIcon(R.drawable.ic_logo_24dp) })
            if(user.flags.earlySupporter) addView(UserInfoChip(context).apply {
                setIcon(R.drawable.ic_star_24dp)
                tint = 0xFFE2EC56.toInt()
            })
            addView(UserInfoChip(context).apply {
                label = "Joined ${TimeUtils.getShortDateString(user.joinedTimestamp)}"
                setIcon(R.drawable.ic_calender_24dp)
            })
        }
    }

    fun updateViewState(viewState: UserProfileHeaderViewModel.ViewState.Loaded) {
        configureActionButtons(viewState)
        configureAvatar(viewState)
        configureBanner(viewState)
        configureBio(viewState)
        configureDisplayName(viewState)
        configureUsername(viewState)
        configureUserDetails(viewState)
        if(viewState.edit) with(binding) {
            details.visibility = GONE
            border.visibility = GONE
            userInfo.visibility = GONE
        }
    }

}