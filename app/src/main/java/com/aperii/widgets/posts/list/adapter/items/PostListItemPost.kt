package com.aperii.widgets.posts.list.adapter.items

import com.aperii.api.post.Post

open class PostListItemPost(
    open val post: Post,
    open val spineType: SpineType
) : PostListItem() {
    enum class SpineType {
        NONE,
        TOP_ONLY,
        BOTTOM_ONLY,
        BOTH
    }
}