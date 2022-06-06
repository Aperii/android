package com.aperii.rest

import com.aperii.api.auth.LoginResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {

    @POST("auth/login")
    suspend fun login(@Body credentials: RestAPIParams.LoginBody): Response<LoginResult>

}