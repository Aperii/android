package com.aperii.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.aperii.R
import com.aperii.models.user.MeUser
import com.aperii.stores.StoreShelves
import com.aperii.utilities.Logger
import com.aperii.utilities.SettingsUtils
import com.aperii.utilities.Utils
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observeAndCatch
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.widgets.auth.WidgetAuthLanding
import com.aperii.widgets.auth.WidgetSplash
import com.aperii.widgets.debugging.WidgetDebugging
import com.aperii.widgets.debugging.WidgetFatalCrash
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.tabs.WidgetTabsHost
import retrofit2.HttpException

open class AppActivity : AppCompatActivity() {

    lateinit var prefs: SettingsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            openScreen<WidgetSplash>()
            prefs = SettingsUtils(getSharedPreferences("PREFS", MODE_PRIVATE))
            configureAuth()
            Coil.setImageLoader(
                ImageLoader.Builder(this@AppActivity).componentRegistry {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder(this@AppActivity))
                    } else {
                        add(GifDecoder())
                    }
                }.crossfade(true).build()
            )
            Utils.appActivity = this
            Logger.log("Application activity initialized")
        } catch (error: Throwable) {
            Logger.default.error("Error initializing activity", error)
            openScreen<WidgetFatalCrash>()
        }
    }

    open fun onAction(action: String?, isAuthed: Boolean) {
        when (action) {
            "com.aperii.intents.actions.DEBUG" -> openScreen<WidgetDebugging>(
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

    class Action : AppActivity() {

        override fun configureAuth() {
            onAction(intent.action, false)
            prefs["APR_auth_tok", ""].run {
                if (isBlank())
                    openScreen<WidgetAuthLanding>()
                else {
                    RestAPI.getInstance(this).getMe().observeAndCatch({
                        StoreShelves.users.me = MeUser.fromApi(this)
                        openScreen<WidgetTabsHost>()
                        onAction(intent.action, true)
                    }, {
                        if (this is HttpException && code() == 403) {
                            prefs.clear("APR_auth_tok")
                            openScreen<WidgetAuthLanding>()
                        } else openScreen<WidgetFatalCrash>()
                    })

                }
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