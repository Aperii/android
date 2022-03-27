package com.aperii.stores

import com.aperii.api.post.Post
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.await

class StorePosts {
    private val snapshot = HashMap<String, Post>()

    fun getPost(id: String) = snapshot[id]
    fun updatePost(post: Post) {
        snapshot[post.id] = post
    }

    fun fetchPost(id: String): Post? {
        if (snapshot.containsKey(id)) return getPost(id)
        return RestAPI.INSTANCE.getPost(id).await().first
    }
}