package com.aperii.utilities.rest

import android.os.Build
import com.aperii.BuildConfig
import com.aperii.api.user.UserFlags
import com.aperii.models.user.MeUser
import com.aperii.rest.RestAPIParams
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

class RestAPI(private val token: String) {

    private val restApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(provideOkHttp())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(com.aperii.rest.RestAPI::class.java)

    companion object {
        val USER_AGENT =
            "Aperii/Mobile-Android (${Build.MODEL}/${Build.DEVICE} SDK${Build.VERSION.SDK_INT})"
        val EMPTY_USER = MeUser(
            "0",
            Date().time,
            "",
            "EMPTY_USER",
            "EMPTY_USER",
            false,
            UserFlags(
                staff = false,
                verified = false,
                admin = false,
                earlySupporter = false
            ),
            false,
            "",
            "",
            "",
            ""
        )

        lateinit var INSTANCE: RestAPI
        fun getInstance(token: String): RestAPI {
            INSTANCE = RestAPI(token)
            Logger("HTTP").debug(USER_AGENT)
            return INSTANCE
        }
    }

    private class HttpLogger : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Logger("HTTP").verbose(message)
        }
    }

    private fun provideOkHttp() = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Content-Type", "application/json")
                .addHeader(
                    "Authorization",
                    token
                )
                .build()

            chain.proceed(request)
        }
        .cache(
            Cache(
                directory = File(Utils.appContext.cacheDir, "apr_ch"),
                maxSize = 50L * 1024L * 1024L
            )
        )
        .retryOnConnectionFailure(true)
        .addInterceptor(HttpLoggingInterceptor(HttpLogger()).apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("authorization")
        })
        .build()

    fun getMe() = restApi.getMe()

    fun getMePosts() = restApi.getMePosts()

    fun getUser(userId: String) = restApi.getUser(userId)

    fun getUserPosts(userId: String) = restApi.getUserPosts(userId)

    fun getPost(id: String) = restApi.getPost(id)

    fun createPost(body: RestAPIParams.PostBody) = restApi.createPost(body)

    fun getFeed() = restApi.getFeed()

}