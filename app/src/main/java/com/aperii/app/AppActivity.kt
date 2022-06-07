package com.aperii.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.aperii.R
import com.aperii.api.error.ErrorResponse
import com.aperii.api.user.User
import com.aperii.models.user.MeUser
import com.aperii.stores.StoreAuth
import com.aperii.stores.StoreNavigation
import com.aperii.stores.StoreUsers
import com.aperii.utilities.Logger
import com.aperii.utilities.rest.HttpUtils.fold
import com.aperii.utilities.rest.RestAPI
import com.aperii.utilities.rx.RxUtils.observe
import com.aperii.utilities.screens.ScreenManager
import com.aperii.utilities.screens.ScreenManager.openScreen
import com.aperii.utilities.settings.settings
import com.aperii.widgets.auth.WidgetAuthLanding
import com.aperii.widgets.auth.WidgetSplash
import com.aperii.widgets.debugging.WidgetDebugging
import com.aperii.widgets.debugging.WidgetFatalCrash
import com.aperii.widgets.posts.create.WidgetPostCreate
import com.aperii.widgets.tabs.NavigationTab
import com.aperii.widgets.tabs.WidgetTabsHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

open class AppActivity : AppCompatActivity() {
    var transition = ScreenManager.Animations.SCALE_CENTER
    private val nav: StoreNavigation by inject()

    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.observeViewState().observe(this::configureUI)
        navigate(intent)
    }

    open fun configureUI(state: MainViewModel.MainState) {
        when(state) {
            is MainViewModel.MainState.LoggedOut -> openScreen<WidgetAuthLanding>()
            is MainViewModel.MainState.LoggedIn -> onAction(intent.action, true)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(transition[2], transition[3])
    }

    private fun navigate(intent: Intent) {
        (intent.extras?.get(ScreenManager.EXTRA_SCREEN ) as Class<Fragment>?)?.run {
            transition = intent.extras?.getIntArray(ScreenManager.EXTRA_ANIM)?.toList() ?: transition
            overridePendingTransition(transition[0], transition[1])
            openScreen(
                newInstance(),
                animation = listOf(0,0,0,0),
                data = intent.extras?.getBundle(ScreenManager.EXTRA_DATA) ?: Bundle()
            )
        }
    }

    open fun onAction(action: String?, isAuthed: Boolean) {
        if(isAuthed) {
            when (action) {
                "com.aperii.intents.actions.DEBUG" -> (this as Context).openScreen<WidgetDebugging>()
                "com.aperii.intents.actions.PROFILE" -> nav.navigationTab = NavigationTab.PROFILE
            }
            openScreen<WidgetTabsHost>()
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
                viewModel.checkAuth()
                Logger.log("Application activity initialized")
            } catch (error: Throwable) {
                Logger.default.error("Error initializing activity", error)
                openScreen<WidgetFatalCrash>()
            }
        }
    }

    class Action : AppActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            viewModel.checkAuth()
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

        override fun configureUI(state: MainViewModel.MainState) {
            when(state) {
                is MainViewModel.MainState.LoggedOut -> openScreen<WidgetAuthLanding>()
                is MainViewModel.MainState.LoggedIn -> onAction(intent.action, true)
            }
        }

        override fun onBackPressed() {
            super.onBackPressed()
            finishAffinity()
        }
    }


}

class MainViewModel(private val api: RestAPI, private val users: StoreUsers): AppViewModel<MainViewModel.MainState>() {

    open class MainState {
        class Uninitialized : MainState()
        class LoggedOut: MainState()
        class LoggedIn: MainState()
    }

    init {
        updateViewState(MainState.Uninitialized())
    }

    fun checkAuth() {
        if(api.currentToken.isBlank()) return updateViewState(MainState.LoggedOut())
        viewModelScope.launch(Dispatchers.IO) {
            api.getMe().fold<User, ErrorResponse>(
                onError = {
                    if(it.code == 403) {
                        api.currentToken = ""
                    }
                    updateViewState(MainState.LoggedOut())
                },
                onSuccess = {
                    users.me = MeUser.fromApi(it)
                    updateViewState(MainState.LoggedIn())
                }
            )
        }
    }
}