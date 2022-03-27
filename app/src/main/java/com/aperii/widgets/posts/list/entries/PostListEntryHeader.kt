package com.aperii.widgets.posts.list.entries

import android.view.ViewGroup
import com.aperii.R
import com.aperii.widgets.posts.list.adapter.items.PostListItem
import com.aperii.widgets.posts.list.adapter.items.PostListItemHeader
import com.aperii.widgets.user.profile.UserProfileHeaderView
import com.aperii.widgets.user.profile.UserProfileHeaderViewModel

class PostListEntryHeader(root: ViewGroup) : PostListEntry(R.layout.post_list_item_header, root) {
    private val header: UserProfileHeaderView = itemView.findViewById(R.id.header)
    override fun bind(item: PostListItem) {
        header.updateViewState(UserProfileHeaderViewModel.ViewState.Loaded((item as PostListItemHeader).user))
    }
}