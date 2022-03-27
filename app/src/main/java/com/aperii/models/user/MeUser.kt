package com.aperii.models.user

import com.aperii.api.user.UserFlags

data class MeUser(
    override val id: String,
    override val joinedTimestamp: Long,
    val email: String,
    override val displayName: String,
    override val username: String,
    val verifiedEmail: Boolean,
    override val flags: UserFlags,
    override val suspended: Boolean,
    override val bio: String,
    override val banner: String,
    override val avatar: String,
    override val pronouns: String
) : User {

    companion object {

        fun fromApi(
            apiUser: com.aperii.api.user.User,
        ) = with(apiUser) {
            MeUser(
                id = id,
                joinedTimestamp = joinedTimestamp,
                email = email,
                displayName = displayName,
                username = username,
                verifiedEmail = verifiedEmail,
                flags = flags,
                suspended = suspended,
                bio = bio,
                banner = banner,
                avatar = avatar,
                pronouns = pronouns
            )
        }

    }

}