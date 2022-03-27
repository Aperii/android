package com.aperii.widgets.posts.list.adapter.items

import com.aperii.api.post.Post

data class PostListItemJumbo(
    override val post: Post,
    override val spineType: SpineType
) : PostListItemPost(post, spineType)