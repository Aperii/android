package com.aperii.rest

class RestAPIParams {

    data class LoginBody(
        val username: String,
        val password: String
    )

    data class PostBody(
        val body: String
    )

    data class EditProfileBody(
        val displayName: String?,
        val bio: String?,
        val pronouns: String?
    )

}