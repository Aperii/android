package com.aperii.api.user

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("joinedTimestamp") val joinedTimestamp: Long,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("username") val username: String,
    @SerializedName("flags") val flags: UserFlags,
    @SerializedName("suspended") val suspended: Boolean,
    @SerializedName("bio") val bio: String,
    @SerializedName("banner") val banner: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("email") val email: String,
    @SerializedName("verifiedEmail") val verifiedEmail: Boolean,
    @SerializedName("pronouns") val pronouns: String
)