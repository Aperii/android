package com.aperii.models.threads

import com.aperii.api.post.Post

class Thread {
    var posts = listOf<Post>()
    var focusedId = ""

    companion object {

        fun fromList(list: List<Post>) = Thread().apply {
            posts = list
        }

        fun Post.toThread(): Thread {
            val list = mutableListOf<Post>()
            addReferenceToList(this, list)
            list.reverse()
            return Thread().apply {
                posts = list
                focusedId = id
            }
        }

        private fun addReferenceToList(post: Post, list: MutableList<Post>): MutableList<Post> {
            list.add(post)
            if (post.referencedPost != null) return addReferenceToList(post.referencedPost, list)
            return list
        }

    }

    fun getFocused() = posts.find {
        it.id == focusedId
    }

    operator fun get(id: String) = posts.find {
        it.id == id
    }

}