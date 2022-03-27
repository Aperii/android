package com.aperii.api.post

import com.google.gson.annotations.SerializedName

data class PostPartial(
    @SerializedName("id") val id: String,
    @SerializedName("createdTimestamp") val createdTimestamp: Long,
    @SerializedName("author") val author: String,
    @SerializedName("body") val body: String,
    @SerializedName("in_reply_to") val referencedPost: String,
    @SerializedName("media") val media: List<String>
)