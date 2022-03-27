package com.aperii.utilities.rest

import com.aperii.BuildConfig
import com.aperii.api.auth.LoginResult
import com.aperii.rest.RestAPIParams
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AuthAPI {

    private val restApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(provideOkHttp())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(com.aperii.rest.AuthAPI::class.java)

    companion object {
        lateinit var INSTANCE: AuthAPI
        fun getInstance(): AuthAPI {
            INSTANCE = AuthAPI()
            return INSTANCE
        }
    }

    private fun provideOkHttp() = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()

            chain.proceed(request)
        }
        .build()

    fun login(credentials: RestAPIParams.LoginBody): Observable<LoginResult> {
        return restApi.login(credentials)
    }

}