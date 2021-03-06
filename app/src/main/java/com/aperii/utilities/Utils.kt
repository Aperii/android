package com.aperii.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.aperii.utilities.rest.PingEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Utils {
    private val threadPool: ExecutorService = Executors.newCachedThreadPool()

    fun runOnMainThread(block: () -> Unit) = Handler(Looper.getMainLooper()).post(block)

    fun runInThread(block: (() -> Unit)) = threadPool.execute(block)

    fun Context.setClipboard(content: String) {
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Aperii", content))
    }

    fun Context.showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun Context.openUrl(url: String) =
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))

    fun Iterable<PingEvent>.average() = sumOf { it.duration } / count()

    inline fun <reified T> InputStream.readJson() =
        GsonBuilder().enableComplexMapKeySerialization().create().fromJson(
            String(readBytes()), T::class.java
        )

    fun <K, V> InputStream.readJsonMap() =
        Gson().fromJson<Map<K, V>>(String(readBytes()), (object : TypeToken<Map<K, V>>() {}).type)


}