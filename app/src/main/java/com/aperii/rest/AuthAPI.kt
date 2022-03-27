package com.aperii.rest

import com.aperii.api.auth.LoginResult
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {

    @POST("auth/login")
    fun login(@Body credentials: RestAPIParams.LoginBody): Observable<LoginResult>

}