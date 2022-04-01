package com.aperii.utilities.screens

import android.os.Bundle
import androidx.fragment.app.*
import com.aperii.R
import com.aperii.utilities.Logger

object ScreenManager {

    val logger = Logger("ScreenManager")

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