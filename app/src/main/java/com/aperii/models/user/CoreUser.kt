package com.aperii.models.user

import com.aperii.api.user.UserFlags

data class CoreUser(
    override val id: String,
    override val joinedTimestamp: Long,
    override val displayName: String,
    override val username: String,
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
            CoreUser(
                id = id,
                joinedTimestamp = joinedTimestamp,
                displayName = displayName,
                username = username,
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