package com.aperii.utilities.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.aperii.BuildConfig
import com.aperii.app.AppActivity
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.await
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

object UpdateUtils {
    val logger = Logger("Updater")

    data class Release(
        @SerializedName("tag_name") val versionCode: Int,
        @SerializedName("name") val versionName: String
    )

    interface GithubApi {
        @GET("repos/Aperii/android/releases/latest")
        fun getLatestRelease(): Observable<Release>

        @Streaming
        @GET("https://github.com/Aperii/android/releases/download/{versionCode}/app-release.apk")
        fun downloadRelease(@Path("versionCode") versionCode: Int) : Call<ResponseBody>
    }

    private val ghApi = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(provideOkHttp())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(GithubApi::class.java)

    private fun provideOkHttp() = OkHttpClient.Builder()
        .cache(
            Cache(
                directory = File(Utils.appContext.cacheDir, "apr_ch"),
                maxSize = 50L * 1024L * 1024L
            )
        )
        .retryOnConnectionFailure(true)
        .build()

    private fun getLatestRelease() = ghApi.getLatestRelease()

    fun checkUpdate(): Pair<Boolean, Release?> {
        val latch = CountDownLatch(1)
        val resRef = AtomicReference<Boolean>()
        val releaseRef = AtomicReference<Release?>()
        getLatestRelease().observeAndCatch({
            logger.logObject(Logger.LoggedItem.Type.DEBUG, this)
            resRef.set(versionCode > BuildConfig.VERSION_CODE)
            releaseRef.set(this)
            latch.countDown()
        }) {
            resRef.set(false)
            latch.countDown()
        }
        if (latch.count != 0L)
            latch.await()

        logger.debug("Has update: ${resRef.get()}")
        return resRef.get() to releaseRef.get()
    }

    fun downloadUpdate(context: Context, release: Release) {
        val apk = File(context.filesDir,"update.apk")
        thread(start = true) {
            ghApi.downloadRelease(release.versionCode).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val uri = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", apk) else Uri.fromFile(apk)
                    FileOutputStream(apk).run {
                        write(response.body()?.bytes())
                        close()
                    }
                    Intent(Intent.ACTION_VIEW).run {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(uri, "application/vnd.android.package-archive")
                        context.startActivity(this)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Utils.showToast("Couldn't update the app")
                }
            })
        }
    }
}