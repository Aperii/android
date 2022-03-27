package com.aperii.widgets.posts.list.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aperii.widgets.posts.list.adapter.items.*
import com.aperii.widgets.posts.list.entries.*

class WidgetPostListAdapter : RecyclerView.Adapter<PostListEntry>() {
    private var mItems: List<PostListItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListEntry {
        return when (viewType) {
            0 -> PostListEntryLoading(parent)
            1 -> PostListEntryText(parent, false)
            2 -> PostListEntryText(parent, true)
            99 -> PostListEntryHeader(parent)
            else -> PostListEntryEmpty(parent)
        }
    }

    override fun onBindViewHolder(holder: PostListEntry, position: Int) =
        holder.bind(mItems[position])

    override fun getItemCount(): Int = mItems.size

    override fun getItemViewType(position: Int): Int {
        return when (mItems[position]) {
            is PostListItemLoading -> 0
            is PostListItemJumbo -> 2
            is PostListItemPost -> 1
            is PostListItemHeader -> 99
            else -> -1
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<PostListItem>) {
        mItems = items
        notifyDataSetChanged()
    }
}