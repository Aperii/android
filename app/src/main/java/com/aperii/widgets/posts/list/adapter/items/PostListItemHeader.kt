package com.aperii.widgets.posts.list.adapter.items

import com.aperii.models.user.User

data class PostListItemHeader(
    val user: User
) : PostListItem()