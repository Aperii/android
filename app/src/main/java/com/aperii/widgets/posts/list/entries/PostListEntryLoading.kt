package com.aperii.widgets.posts.list.entries

import android.view.ViewGroup
import com.aperii.R
import com.aperii.widgets.posts.list.adapter.items.PostListItem

class PostListEntryLoading(root: ViewGroup) : PostListEntry(R.layout.post_list_item_loading, root) {
    override fun bind(item: PostListItem) {}
}