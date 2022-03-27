package com.aperii.api.user

import com.google.gson.annotations.SerializedName

data class UserFlags(
    @SerializedName("staff") val staff: Boolean,
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("admin") val admin: Boolean,
    @SerializedName("early_supporter") val earlySupporter: Boolean
)
