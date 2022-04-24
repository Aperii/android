package com.aperii.api.error

import com.google.gson.annotations.SerializedName

data class FieldError(
    @SerializedName("status") val status: Int,
    @SerializedName("error") val error: String,
    @SerializedName("field") val field: String
)