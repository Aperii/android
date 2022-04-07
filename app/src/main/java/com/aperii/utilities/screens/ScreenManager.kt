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

    const val EXTRA_SCREEN = "com.aperii.intents.extras.EXTRA_SCREEN"
    const val EXTRA_BACK = "com.aperii.intents.extras.EXTRA_ALLOW_BACK"
    const val EXTRA_ANIM = "com.aperii.intents.extras.EXTRA_ANIMATION"
    const val EXTRA_DATA = "com.aperii.intents.extras.EXTRA_DATA"


    object Animations {
        val SLIDE_FROM_RIGHT = listOf(
            R.anim.slide_from_right,
            R.anim.slide_to_left,
            R.anim.slide_from_left,
            R.anim.slide_to_right
        )
        val SCALE_CENTER = listOf(
            R.anim.fade_open_in,
            R.anim.fade_open_out,
            R.anim.fade_close_in,
            R.anim.fade_close_out
        )
        val TEST = listOf(
            android.R.anim.slide_out_right,
            android.R.anim.slide_out_right,
            android.R.anim.slide_out_right,
            android.R.anim.slide_out_right
        )
    }

    inline fun <reified T: Fragment> openScreen(ctx: Context, allowBack: Boolean = false, animation: List<Int> = listOf(), data: Bundle = Bundle()) {
        Intent(Intent.ACTION_VIEW).apply {
            putExtra(EXTRA_SCREEN, T::class.java)
            putExtra(EXTRA_BACK, allowBack)
            putExtra(EXTRA_ANIM, animation.toIntArray())
            putExtra(EXTRA_DATA, data)
            setClass(ctx, AppActivity.Main::class.java)
            ctx.startActivity(this)
        }
    }

    fun FragmentActivity.openScreen(
        screen: Fragment,
        allowBack: Boolean = false,
        animation: List<Int> = Animations.SLIDE_FROM_RIGHT,
        data: Bundle = Bundle()
    ) = supportFragmentManager.commit {
        logger.verbose("Opening page ${screen::class.java.simpleName}")
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
        logger.verbose("Opening page ${T::class.java.simpleName}")
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