package com.aperii.models.user

import com.aperii.api.user.UserFlags

interface User {
    val id: String
    val joinedTimestamp: Long
    val displayName: String
    val username: String
    val flags: UserFlags
    val suspended: Boolean
    val bio: String
    val banner: String
    val avatar: String
    val pronouns: String
}