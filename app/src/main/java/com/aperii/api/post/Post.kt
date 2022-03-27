package com.aperii.api.post

import com.aperii.api.user.User
import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id") val id: String,
    @SerializedName("createdTimestamp") val createdTimestamp: Long,
    @SerializedName("author") val author: User,
    @SerializedName("body") val body: String,
    @SerializedName("in_reply_to") val referencedPost: Post?,
    @SerializedName("media") val media: List<String>
)