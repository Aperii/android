package com.aperii.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.aperii.R
import com.aperii.models.user.MeUser
import com.aperii.stores.StoreShelves
import com.aperii.utilities.Logger
import com.aperii.utilities.SettingsUtils
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.update.UpdateUtils
import com.aperii.widgets.auth.WidgetAuthLanding
import com.aperii.widgets.auth.WidgetSplash
import com.aperii.widgets.debugging.WidgetDebugging
import com.aperii.widgets.debugging.WidgetFatalCrash
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.tabs.WidgetTabsHost
import retrofit2.HttpException
import kotlin.concurrent.thread

open class AppActivity : AppCompatActivity() {
    var transition = ScreenManager.Animations.SCALE_CENTER
    var allowBack = true
    companion object {
        lateinit var prefs: SettingsUtils
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = SettingsUtils(getSharedPreferences("PREFS", MODE_PRIVATE))
        navigate(intent)
    }

    override fun onBackPressed() {
        if(!allowBack) return finishAffinity()
        super.onBackPressed()
        overridePendingTransition(transition[2], transition[3])
    }

    private fun navigate(intent: Intent) {
        (intent.extras?.get(ScreenManager.EXTRA_SCREEN ) as Class<Fragment>?)?.run {
            transition = intent.extras?.getIntArray(ScreenManager.EXTRA_ANIM)?.toList() ?: transition
            allowBack = intent.extras?.getBoolean(ScreenManager.EXTRA_BACK) ?: false
            overridePendingTransition(transition[0], transition[1])
            openScreen(
                newInstance(),
                animation = listOf(0,0,0,0),
                data = intent.extras?.getBundle(ScreenManager.EXTRA_DATA) ?: Bundle()
            )
        }
    }

    open fun onAction(action: String?, isAuthed: Boolean) {
        when (action) {
            "com.aperii.intents.actions.DEBUG" -> (this as Context).openScreen<WidgetDebugging>(
                allowBack = true,
                animation = ScreenManager.Animations.SCALE_CENTER
            )
        }
    }

    open fun configureAuth() {
        prefs["APR_auth_tok", ""].run {
            if (isBlank())
                openScreen<WidgetAuthLanding>()
            else {
                RestAPI.getInstance(this).getMe().observeAndCatch({
                    StoreShelves.users.me = MeUser.fromApi(this)
                    openScreen<WidgetTabsHost>(animation = ScreenManager.Animations.SCALE_CENTER)
                    onAction(intent.action, true)
                }, {
                    if (this is HttpException && code() == 403) {
                        prefs.clear("APR_auth_tok")
                        openScreen<WidgetAuthLanding>()
                    } else {
                        supportFragmentManager.findFragmentById(R.id.widget_host_fragment)?.run {
                            onSaveInstanceState(Bundle().apply {
                                putBoolean("canRefresh", true)
                            })
                        }
                    }
                })
            }
        }
    }

    class Main : AppActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            try {
                openScreen<WidgetSplash>()
                Coil.setImageLoader(
                    ImageLoader.Builder(this@Main).componentRegistry {
                        if (Build.VERSION.SDK_INT >= 28) {
                            add(ImageDecoderDecoder(this@Main))
                        } else {
                            add(GifDecoder())
                        }
                    }.crossfade(true).build()
                )
                configureAuth()
                Logger.log("Application activity initialized")
            } catch (error: Throwable) {
                Logger.default.error("Error initializing activity", error)
                openScreen<WidgetFatalCrash>()
            }
        }

//        override fun onNewIntent(intent: Intent?) {
//            super.onNewIntent(intent)
//            val screen = intent?.extras?.get(ScreenManager.EXTRA_SCREEN ) as Class<Fragment>?
//            if (screen != null) {
//                val anim = intent?.extras?.getIntArray(ScreenManager.EXTRA_ANIM)?.toList() ?: listOf(0,0,0,0)
//                overridePendingTransition(anim[0], anim[2])
//                openScreen(
//                    screen.newInstance(),
//                    intent?.extras?.getBoolean(ScreenManager.EXTRA_BACK) ?: false,
//                    intent?.extras?.getIntArray(ScreenManager.EXTRA_ANIM)?.toList() ?: listOf(0,0,0,0),
//                    intent?.extras?.getBundle(ScreenManager.EXTRA_DATA) ?: Bundle()
//                )
//            }
//        }

    }

    class Action : AppActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            configureAuth()
        }

        override fun onNewIntent(intent: Intent?) {
            super.onNewIntent(intent)
            intent?.run {
                onAction(action, true)
            }
        }

        override fun onAction(action: String?, isAuthed: Boolean) {
            when (action) {
                Intent.ACTION_SEND -> if (isAuthed) Bundle().run {
                    putString(
                        WidgetPostCreate.EXTRA_MESSAGE,
                        intent.extras?.getString(Intent.EXTRA_TEXT)
                    )
                    putBoolean(WidgetPostCreate.EXTRA_CLOSE_ON_EXIT, true)
                    openScreen<WidgetPostCreate>(
                        allowBack = true,
                        data = this,
                        animation = ScreenManager.Animations.SCALE_CENTER
                    )
                }
            }
        }

        override fun onBackPressed() {
            super.onBackPressed()
            finishAffinity()
        }
    }


}