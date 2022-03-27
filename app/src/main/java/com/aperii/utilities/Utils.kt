package com.aperii.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.aperii.app.AppActivity

object Utils {
    lateinit var appContext: Context
    lateinit var appActivity: AppActivity
    val mainThread = Handler(Looper.getMainLooper())

    fun setClipboard(content: String) {
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Aperii", content))
    }

    fun showToast(text: String) {
        Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
    }

}