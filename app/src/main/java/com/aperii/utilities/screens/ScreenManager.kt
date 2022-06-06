package com.aperii.utilities.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.*
import com.aperii.R
import com.aperii.app.AppActivity
import com.aperii.utilities.Logger


object ScreenManager {

    val logger = Logger("ScreenManager")

    val EXTRA_SCREEN by extras()
    val EXTRA_ANIM by extras()
    val EXTRA_DATA by extras()

    object Animations {
        val SLIDE_FROM_RIGHT = listOf(
            R.anim.slide_from_right,
            R.anim.slide_zoom_out,
            R.anim.slide_zoom_in,
            R.anim.slide_to_right
        )
        val SCALE_CENTER = listOf(
            R.anim.fade_open_in,
            R.anim.fade_open_out,
            R.anim.fade_close_in,
            R.anim.fade_close_out
        )
    }

    inline fun <reified T: Fragment> Context.openScreen(allowBack: Boolean = true, animation: List<Int> = Animations.SCALE_CENTER, data: Bundle = Bundle(), screen: T? = null) {
        Intent(Intent.ACTION_VIEW).apply {
            putExtra(EXTRA_SCREEN, if(screen != null) screen::class.java else T::class.java)
            putExtra(EXTRA_ANIM, animation.toIntArray())
            putExtra(EXTRA_DATA, data)
            if(!allowBack) {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            setClass(this@openScreen, AppActivity::class.java)
            logger.verbose("[${this@openScreen::class.java.simpleName}] > [${T::class.java.simpleName}]")
            this@openScreen.startActivity(this)
        }
    }

    fun FragmentActivity.openScreen(
        screen: Fragment,
        allowBack: Boolean = false,
        animation: List<Int> = Animations.SLIDE_FROM_RIGHT,
        data: Bundle = Bundle()
    ) = supportFragmentManager.commit {
        setCustomAnimations(animation)
        screen.arguments = data
        replace(R.id.widget_host_fragment, screen)
        if (allowBack) addToBackStack(null) else supportFragmentManager.popBackStack()
    }

    inline fun <reified T : Fragment> FragmentActivity.openScreen(
        allowBack: Boolean = false,
        animation: List<Int> = Animations.SLIDE_FROM_RIGHT,
        data: Bundle = Bundle()
    ) = supportFragmentManager.commit {
        setCustomAnimations(animation)
        replace<T>(R.id.widget_host_fragment, args = data)
        if (allowBack) addToBackStack(null) else supportFragmentManager.popBackStack()
    }

    fun Fragment.refresh() = parentFragmentManager.commit {
        detach(this@refresh)
        attach(this@refresh)
    }

    fun FragmentTransaction.setCustomAnimations(anim: List<Int>) {
        if (anim.isEmpty()) return
        when (anim.size) {
            2 -> setCustomAnimations(anim[0], anim[1])
            4 -> setCustomAnimations(anim[0], anim[1], anim[2], anim[3])
            else -> throw IllegalStateException("Must contain only two or four values")
        }
    }
}