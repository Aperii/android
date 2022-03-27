package com.aperii.rest

import com.aperii.api.post.Post
import com.aperii.api.post.PostPartial
import com.aperii.api.user.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RestAPI {

    @GET("users/@me")
    fun getMe(): Observable<User>

    @GET("users/@me/posts")
    fun getMePosts(): Observable<List<Post>>

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: String): Observable<User>

    @GET("users/{userId}/posts")
    fun getUserPosts(@Path("userId") userId: String): Observable<List<Post>>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: String): Observable<Post>

    @POST("users/@me/posts")
    fun createPost(@Body body: RestAPIParams.PostBody): Observable<PostPartial>

    @GET("posts/all")
    fun getFeed(): Observable<List<Post>>

}