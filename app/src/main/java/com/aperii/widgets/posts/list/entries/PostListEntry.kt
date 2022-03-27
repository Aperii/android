package com.aperii.widgets.posts.list.entries

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.aperii.widgets.posts.list.adapter.items.PostListItem

abstract class PostListEntry(@LayoutRes resId: Int, root: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(root.context).inflate(resId, root, false)) {
    abstract fun bind(item: PostListItem)
}