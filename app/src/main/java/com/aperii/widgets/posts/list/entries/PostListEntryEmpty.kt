package com.aperii.widgets.posts.list.entries

import android.view.ViewGroup
import com.aperii.R
import com.aperii.widgets.posts.list.adapter.items.PostListItem

class PostListEntryEmpty(viewGroup: ViewGroup) :
    PostListEntry(R.layout.post_list_item_empty, viewGroup) {
    override fun bind(item: PostListItem) {}
}