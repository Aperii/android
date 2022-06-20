package com.aperii.widgets.posts.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.aperii.R
import com.aperii.api.user.User
import com.aperii.databinding.WidgetPostPreviewBinding
import com.aperii.stores.StorePosts
import com.aperii.utilities.color.ColorUtils.getThemedColor
import com.aperii.utilities.images.IconUtils.setAvatar
import com.aperii.utilities.screens.extras
import com.aperii.utilities.text.TextUtils.renderPost
import com.aperii.utilities.time.TimeUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class WidgetPostPreview : BottomSheetDialogFragment() {

    val binding: WidgetPostPreviewBinding by viewBinding(CreateMethod.INFLATE)

    val posts: StorePosts by inject()

    companion object {
        val EXTRA_POST by extras()

        fun open(fm: FragmentManager, postId: String) = WidgetPostPreview().run {
            arguments = Bundle().apply {
                putString(EXTRA_POST, postId)
            }
            show(fm, this::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        configureUI()
        dialog?.apply {
            setOnShowListener {
                findViewById<View?>(R.id.design_bottom_sheet)?.setBackgroundColor(
                    context.getThemedColor(
                        R.attr.bottomSheetBackground
                    )
                )
            }
        }
        return binding.root
    }

    private fun configureUI() {
        if (arguments == null || arguments?.getString(EXTRA_POST) == null) dismiss()
        val post = posts[arguments?.getString(EXTRA_POST)!!]!!
        binding.post.border.visibility = View.GONE
        configureAvatar(post.author)
        configureBody(post.body)
        configurePronouns(post.author)
        configureTimeStamp(post.createdTimestamp)
        configureUserAndDisplayName(post.author)
    }

    private fun configureAvatar(author: User) = author.setAvatar(binding.post.avatar)

    private fun configurePronouns(author: User) = binding.post.pronouns.apply {
        text = context.getString(R.string.separated, author.pronouns)
        visibility = if (author.pronouns.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun configureTimeStamp(timestamp: Long) = binding.post.timestamp.run {
        text = TimeUtils.getLongTimeString(timestamp)
    }

    private fun configureBody(text: String) = binding.post.postBodySpannable.run {
        renderPost(text)
    }

    private fun configureUserAndDisplayName(author: User) = author.run {
        binding.post.displayName.text = displayName
        binding.post.username.text =
            binding.post.username.context.getString(R.string.username, username)
        binding.post.badge.visibility = if (flags.verified) View.VISIBLE else View.GONE
    }

}