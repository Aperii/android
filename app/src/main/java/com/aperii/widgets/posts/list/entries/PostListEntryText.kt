package com.aperii.widgets.posts.list.entries

import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.api.post.Post
import com.aperii.api.user.User
import com.aperii.stores.StoreShelves
import com.aperii.utilities.text.TextUtils.renderPost
import com.aperii.utilities.time.TimeUtils
import com.aperii.widgets.posts.WidgetPost
import com.aperii.widgets.posts.list.adapter.items.PostListItem
import com.aperii.widgets.posts.list.adapter.items.PostListItemPost
import com.aperii.widgets.user.profile.WidgetProfile

class PostListEntryText(root: ViewGroup, private val shouldJumbo: Boolean) : PostListEntry(
    if (shouldJumbo) R.layout.post_list_item_jumbo else R.layout.post_list_item,
    root
) {
    private val author: TextView = itemView.findViewById(R.id.display_name)
    private val username: TextView = itemView.findViewById(R.id.username)
    private val badge: ImageView = itemView.findViewById(R.id.badge)
    private val body: TextView = itemView.findViewById(R.id.post_body_spannable)
    private val avatar: ImageView = itemView.findViewById(R.id.avatar)
    private val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    private val pronouns: TextView = itemView.findViewById(R.id.pronouns)

    private val spineTop: View? = itemView.findViewById(R.id.spine_top)
    private val spineBottom: View = itemView.findViewById(R.id.spine_bottom)
    private val border: View = itemView.findViewById(R.id.border)

    override fun bind(item: PostListItem) {
        val post = (item as PostListItemPost).post
        StoreShelves.posts.updatePost(post)

        configureSpine(item.spineType)
        configureText(post)
        configureAuthor(post.author)
        configureTimestamp(post.createdTimestamp)

        itemView.setOnClickListener {
            if (!shouldJumbo) WidgetPost.open(post.id)
        }
    }

    private fun configureTimestamp(createdAt: Long) = timestamp.run {
        text = if(shouldJumbo) TimeUtils.getLongTimeString(createdAt) else context.getString(R.string.separated, TimeUtils.getShortTimeString(createdAt))
    }

    private fun configureAuthor(user: User) = user.run {
        author.text = displayName
        configureUsername(username)
        configureAvatar(user)
        configurePronouns(user.pronouns)
        badge.visibility = if (flags.verified) View.VISIBLE else View.GONE
    }

    private fun configurePronouns(pronouns: String) = this@PostListEntryText.pronouns.apply {
        text = context.getString(R.string.separated, pronouns)
    }

    private fun configureAvatar(user: User) = avatar.apply {
        setOnClickListener {
            WidgetProfile.open(user.id)
        }
        load(if (user.avatar.isNotEmpty()) "https://api.aperii.com/cdn/avatars/${user.avatar}" else "https://aperii.com/av.png") {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.img_default_avatar)
        }
    }

    private fun configureUsername(username: String) = this@PostListEntryText.username.run{
        text = context.getString(R.string.username, username)
    }

    private fun configureText(post: Post) = body.apply {
        renderPost(post.body.trimIndent())
        movementMethod = LinkMovementMethod()
    }

    private fun configureSpine(spineType: PostListItemPost.SpineType) {
        when(spineType) {
            PostListItemPost.SpineType.TOP_ONLY -> {
                spineTop?.visibility = View.VISIBLE
                spineBottom.visibility = View.GONE
                border.visibility = View.GONE
            }
            PostListItemPost.SpineType.BOTTOM_ONLY -> {
                spineTop?.visibility = View.GONE
                spineBottom.visibility = View.VISIBLE
            }
            PostListItemPost.SpineType.BOTH -> {
                spineTop?.visibility = View.VISIBLE
                spineBottom.visibility = View.VISIBLE
                border.visibility = View.GONE
            }
            else -> {
                spineTop?.visibility = View.GONE
                spineBottom.visibility = View.GONE
            }
        }
    }

}