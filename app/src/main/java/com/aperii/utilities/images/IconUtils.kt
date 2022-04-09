package com.aperii.utilities.images

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.models.user.CoreUser
import com.aperii.models.user.User

object IconUtils {

    fun User.setAvatar(iv: ImageView) = iv.load(if (avatar.isNotEmpty()) "https://api.aperii.com/cdn/avatars/${avatar}" else "https://aperii.com/av.png") {
        transformations(CircleCropTransformation())
        placeholder(R.drawable.img_default_avatar)
    }

    fun com.aperii.api.user.User.setAvatar(iv: ImageView) = CoreUser.fromApi(this).setAvatar(iv)

    fun ImageView.setAvatar(user: User) = user.setAvatar(this)

}