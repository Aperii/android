package com.aperii.utilities.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.core.content.FileProvider
import androidx.loader.content.AsyncTaskLoader
import com.aperii.BuildConfig
import com.aperii.utilities.Logger
import com.aperii.utilities.Utils
import com.aperii.utilities.Utils.showToast
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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
import java.io.InputStream
import java.io.Serializable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

object UpdateUtils {
    val logger = Logger("Updater")

    data class Release(
        @SerializedName("tag_name") val versionCode: Int,
        @SerializedName("name") val versionName: String,
        @SerializedName("assets") val assets: List<Asset>
    ) : Serializable

    data class Asset(
        @SerializedName("size") val size: Long
    ) : Serializable

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

    fun downloadUpdate(context: Context, release: Release, progressBar: ProgressBar? = null) {
        val apk = File(context.filesDir,"update.apk")
        val uri =
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", apk)
            else
                Uri.fromFile(apk)
        if(apk.exists()) apk.delete()
        Utils.runInThread {
            ghApi.downloadRelease(release.versionCode).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    response.body()?.byteStream()?.let { s ->
                        val stream = ObservableInputStream(s) {
                            progressBar?.progress = it.toInt()
                        }
                        FileOutputStream(apk).run {
                            write(stream.readBytes())
                            close()
                        }
                    }

                    Intent(Intent.ACTION_VIEW).run {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(uri, "application/vnd.android.package-archive")
                        context.startActivity(this)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    context.showToast("Couldn't update the app")
                }
            })
        }
    }
}