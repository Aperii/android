package com.aperii.widgets.posts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aperii.R
import com.aperii.api.post.Post
import com.aperii.app.AppFragment
import com.aperii.databinding.WidgetPostListBinding
import com.aperii.models.threads.Thread
import com.aperii.models.user.User
import com.aperii.widgets.posts.list.adapter.WidgetPostListAdapter
import com.aperii.widgets.posts.list.adapter.items.*

class WidgetPostList : AppFragment() {
    var adapter: WidgetPostListAdapter? = null
    private var mThread: Thread? = null
    private var mShowSpine = false
    lateinit var binding: WidgetPostListBinding
    private lateinit var viewModel: WidgetPostListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.widget_post_list, container, false)
        binding = WidgetPostListBinding.bind(root)
        if (adapter == null) createAdapter() else configureExistingAdapter()
        viewModel = ViewModelProvider(this)[WidgetPostListViewModel::class.java]
        return root
    }

    private fun getPostItems(thread: Thread, user: User?): List<PostListItem> {
        val items = mutableListOf<PostListItem>()
        if (user != null) items.add(PostListItemHeader(user))
        for (post in thread.posts)
            if (post.id == thread.focusedId) items.add(PostListItemJumbo(post, getSpineType(thread, post))) else items.add(PostListItemPost(post, getSpineType(thread, post)))
        return items
    }

    private fun getSpineType(thread: Thread, post: Post) = if(!mShowSpine) PostListItemPost.SpineType.NONE else if (thread.posts.size > 1) when(thread.posts.indexOf(post)){
        0 -> PostListItemPost.SpineType.TOP_ONLY
        thread.posts.lastIndex -> PostListItemPost.SpineType.BOTTOM_ONLY
        else -> PostListItemPost.SpineType.BOTH
    } else PostListItemPost.SpineType.NONE

    fun setSource(thread: Thread, user: User?, showSpines: Boolean = false) {
        mThread = thread
        mShowSpine = showSpines
        adapter?.setItems(getPostItems(thread, user))
    }

    fun addPosts(posts: List<Post>) {
        mShowSpine = false
        adapter?.addItems(getPostItems(Thread.fromList(posts), null))
    }

    private fun createAdapter() {
        adapter = WidgetPostListAdapter().apply {
            setItems(listOf(PostListItemLoading()))
            binding.postListRecycler.also {
                it.adapter = this
                it.layoutManager = LinearLayoutManager(context)
            }
        }

    }

    private fun configureExistingAdapter() {
        binding.postListRecycler.apply {
            adapter = this@WidgetPostList.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}