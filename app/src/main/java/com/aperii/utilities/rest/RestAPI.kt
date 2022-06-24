package com.aperii.utilities.rest

import android.os.Build
import com.aperii.BuildConfig
import com.aperii.api.user.UserFlags
import com.aperii.models.user.MeUser
import com.aperii.rest.RestAPIParams
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils.average
import com.aperii.utilities.settings.settings
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.InetAddress
import java.net.Socket
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class RestAPI {
    private val store by settings()
    val logger = Logger("HTTP")

    var currentToken: String
        get() = store["APR_auth_tok", ""]
        set(value) = store.set("APR_auth_tok", value)

    val pings by lazy { mutableListOf<PingEvent>() }
    val ping get() = pings.average()

    init {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                pings.add(ping())
            }
        }, 0, 10000)
    }

    private val restApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(provideOkHttp())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().disableHtmlEscaping().create()))
        .build()
        .create(com.aperii.rest.RestAPI::class.java)

    companion object {
        val USER_AGENT =
            "Aperii/Mobile-Android@${
                if (BuildConfig.VERSION_CODE == 1300) BuildConfig.VERSION_NAME.split(
                    " - "
                )[1] else BuildConfig.VERSION_CODE
            } (${Build.MODEL}/${Build.DEVICE} SDK${Build.VERSION.SDK_INT})"

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
    }

    private class HttpLogger(val logger: Logger) : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            logger.verbose(message)
        }
    }

    class ResponseInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            if (response.headers["Content-Type"]?.startsWith("application/json") == false) return response
            return response.newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-32")
                .build()
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
                    currentToken
                )
                .build()

            chain.proceed(request)
        }
        .cache(
            Cache(
                directory = File("apr_ch"),
                maxSize = 50L * 1024L * 1024L
            )
        )
        .retryOnConnectionFailure(true)
        .addInterceptor(ResponseInterceptor())
        .addInterceptor(HttpLoggingInterceptor(HttpLogger(logger)).apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("authorization")
        })
        .pingInterval(10, TimeUnit.SECONDS)
        .build()

    fun ping(): PingEvent {
        val start = System.currentTimeMillis()
        return try {
            Socket(
                InetAddress.getByName(URL(BuildConfig.BASE_URL + "hello").host).hostAddress,
                443
            ).close()
            PingEvent(true, System.currentTimeMillis() - start)
        } catch (e: Throwable) {
            logger.error("Error pinging api.aperii.com", e)
            PingEvent(false, System.currentTimeMillis() - start)
        }
    }

    suspend fun getMe() = restApi.getMe()

    suspend fun getMePosts() = restApi.getMePosts()

    suspend fun editProfile(displayName: String?, bio: String?, pronouns: String?) =
        restApi.editProfile(
            RestAPIParams.EditProfileBody(
                displayName, bio, pronouns
            )
        )

    suspend fun getUser(userId: String) = restApi.getUser(userId)

    suspend fun getUserPosts(userId: String) = restApi.getUserPosts(userId)

    suspend fun getPost(id: String) = restApi.getPost(id)

    suspend fun getReplies(id: String) = restApi.getReplies(id)

    suspend fun createPost(body: String, replyTo: String = "") =
        restApi.createPost(RestAPIParams.PostBody(body), replyTo)

    suspend fun getFeed() = restApi.getFeed()

}

data class PingEvent(val successful: Boolean, val duration: Long, val time: Date = Date())