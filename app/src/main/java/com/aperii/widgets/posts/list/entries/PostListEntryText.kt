package com.aperii.widgets.posts.list.entries

import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import coil.transform.CircleCropTransformation
import com.aperii.R
import com.aperii.api.post.Post
import com.aperii.api.user.User
import com.aperii.utilities.DimenUtils.getResizedDrawable
import com.aperii.utilities.color.ColorUtils.getThemedColor
import com.aperii.utilities.text.Renderer
import com.aperii.utilities.text.TextUtils.renderDefault
import com.aperii.utilities.text.nodes.PostRenderContext
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
    private val username: TextView? = itemView.findViewById(R.id.username)
    private val badge: ImageView? = itemView.findViewById(R.id.badge)
    private val body: TextView = itemView.findViewById(R.id.post_body_spannable)
    private val avatar: ImageView = itemView.findViewById(R.id.avatar)
    private val timestamp: TextView? = itemView.findViewById(R.id.timestamp)
    private val pronouns: TextView? = itemView.findViewById(R.id.pronouns)

    private val spineTop: View? = itemView.findViewById(R.id.spine_top)
    private val spineBottom: View = itemView.findViewById(R.id.spine_bottom)
    private val border: View = itemView.findViewById(R.id.border)

    override fun bind(item: PostListItem) {
        val post = (item as PostListItemPost).post

        configureSpine(item.spineType)
        configureText(post)
        configureDisplayName(post.author)
        configureAuthor(post.author)
        configureTimestamp(post.createdTimestamp)

        itemView.setOnClickListener {
            if (!shouldJumbo) WidgetPost.open(it.context, post.id)
        }
    }

    private fun configureTimestamp(createdAt: Long) = timestamp?.run {
        text = if (shouldJumbo) TimeUtils.getLongTimeString(createdAt) else context.getString(
            R.string.separated,
            TimeUtils.getShortTimeString(createdAt)
        )
    }

    private fun configureAuthor(user: User) = user.run {
        configureUsername(username)
        configureAvatar(this)
        configurePronouns(pronouns)
        badge?.visibility = if (flags.verified && shouldJumbo) View.VISIBLE else View.GONE
    }

    private fun configurePronouns(pronouns: String) = this@PostListEntryText.pronouns?.apply {
        text = context.getString(R.string.separated, pronouns)
        visibility = if (pronouns.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun configureAvatar(user: User) = avatar.apply {
        setOnClickListener {
            WidgetProfile.open(it.context, user.id)
        }
        load(if (user.avatar.isNotEmpty()) "https://api.aperii.com/cdn/avatars/${user.avatar}" else "https://aperii.com/av.png") {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.img_default_avatar)
        }
    }

    private fun configureDisplayName(user: User) = author.run {
        if (shouldJumbo) text = user.displayName else {
            val builder = SpannableStringBuilder()
            builder.append(Renderer.renderTwemoji(user.displayName, PostRenderContext(context, lineHeight)))
            builder.setSpan(
                ForegroundColorSpan(context.getThemedColor(R.attr.textOnBackground)),
                0,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val align = if (Build.VERSION.SDK_INT >= 29) 2 else DynamicDrawableSpan.ALIGN_BASELINE
            val badge = context.getResizedDrawable(R.drawable.ic_badge_20dp, 14)!!
            if (user.flags.verified) {
                val len = builder.length
                builder.append(" ✔")
                builder.setSpan(
                    ImageSpan(badge, align),
                    len + 1,
                    builder.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            builder.append(" ${context.getString(R.string.username, user.username)}")
            text = builder
            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                text = TextUtils.ellipsize(
                    builder,
                    paint,
                    width.toFloat(),
                    TextUtils.TruncateAt.END,
                    true
                ) { start, _ ->
                    setCompoundDrawablesRelative(
                        null,
                        null,
                        if (start <= user.displayName.length && start != 0 && user.flags.verified) badge else null,
                        null
                    )
                }
            }
        }
    }

    private fun configureUsername(username: String) = this@PostListEntryText.username?.run {
        text = context.getString(R.string.username, username)
    }

    private fun configureText(post: Post) = body.apply {
        renderDefault(post.body.trimIndent())
        movementMethod = LinkMovementMethod()
    }

    private fun configureSpine(spineType: PostListItemPost.SpineType) {
        when (spineType) {
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