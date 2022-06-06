package com.aperii.utilities.rest

import com.aperii.BuildConfig
import com.aperii.rest.RestAPIParams
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthAPI {

    private val restApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(provideOkHttp())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(com.aperii.rest.AuthAPI::class.java)

    private fun provideOkHttp() = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()

            chain.proceed(request)
        }
        .build()

    suspend fun login(credentials: RestAPIParams.LoginBody) = restApi.login(credentials)

}