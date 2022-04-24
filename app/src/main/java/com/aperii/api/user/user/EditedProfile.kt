package com.aperii.api.user.user

import com.aperii.api.error.FieldError
import com.aperii.api.user.User
import com.google.gson.annotations.SerializedName

data class EditedProfile(
    @SerializedName("profile") val profile: User,
    @SerializedName("errors") val errors: List<FieldError>?
)
