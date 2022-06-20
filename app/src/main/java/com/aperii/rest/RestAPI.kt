package com.aperii.rest

import com.aperii.api.post.Post
import com.aperii.api.post.PostPartial
import com.aperii.api.user.User
import com.aperii.api.user.user.EditedProfile
import retrofit2.Response
import retrofit2.http.*

interface RestAPI {

    @GET("users/@me")
    suspend fun getMe(): Response<User>

    @GET("users/@me/posts")
    suspend fun getMePosts(): Response<List<Post>>

    @PATCH("users/@me")
    suspend fun editProfile(@Body body: RestAPIParams.EditProfileBody): Response<EditedProfile>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @GET("users/{userId}/posts")
    suspend fun getUserPosts(@Path("userId") userId: String): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: String): Response<Post>

    @GET("posts/{id}/replies")
    suspend fun getReplies(@Path("id") id: String): Response<List<Post>>

    @POST("users/@me/posts")
    suspend fun createPost(
        @Body body: RestAPIParams.PostBody,
        @Query("replyto") replyTo: String = ""
    ): Response<PostPartial>

    @GET("posts/all")
    suspend fun getFeed(): Response<List<Post>>

}