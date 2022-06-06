package com.aperii.stores

import com.aperii.api.post.Post
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.settings.settings

class StorePosts(private val api: RestAPI) {
    private val store by settings("StorePosts")

    operator fun get(id: String): Post? = store.getObject(id)
    operator fun set(id: String, post: Post) = store.set(id, post)

    suspend fun create(text: String, replyTo: String = "") = api.createPost(text, replyTo)

    suspend fun fetchPost(id: String): Post? = this[id] ?: api.getPost(id).body()?.also { this[it.id] = it }

    suspend fun fetchPostReplies(id: String): List<Post> = (api.getReplies(id).body() ?: emptyList()).also {
        for(post in it)
            this[post.id] = post
    }
}