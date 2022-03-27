package com.aperii.api.auth

import com.google.gson.annotations.SerializedName

data class LoginResult(
    @SerializedName("token") val token: String
)