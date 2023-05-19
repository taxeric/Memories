package com.lanier.memories.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.lanier.memories.datastore.AppPreference
import com.lanier.memories.entity.AppPreferenceCache
import com.lanier.memories.R

/**
 * Author: Turtledove
 * Date  : on 2023/5/12
 * Desc  :
 */
abstract class BaseAct: AppCompatActivity() {

    protected open val preferenceDataStore by lazy {
        AppPreference(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppPreferenceCache.curThemeValue == -1) {
            AppPreferenceCache.curThemeValue = preferenceDataStore.getTheme()
            AppPreferenceCache.curTheme = when (AppPreferenceCache.curThemeValue) {
                AppPreferenceCache.ThemeDefault -> R.style.Base_Theme_Memories
                AppPreferenceCache.ThemeBlue -> R.style.Base_Theme_Blue
                else -> R.style.Base_Theme_Memories
            }
            if (AppPreferenceCache.curThemeValue == -1) {
                AppPreferenceCache.curThemeValue = 0
            }
        }
        super.onCreate(savedInstanceState)
        setTheme(AppPreferenceCache.curTheme)
        setContentView(layoutId)
        if (immersiveStatusBar) {
            immersive()
        }
        initViews()
    }

    abstract val layoutId: Int
    protected open val immersiveStatusBar: Boolean = false
    protected open fun initViews(){}

    protected open fun getPrimaryColor(darkMode: Boolean): Int {
        return when (AppPreferenceCache.curThemeValue) {
            AppPreferenceCache.ThemeDefault -> {
                if (darkMode) {
                    Color.rgb(209, 188, 255)
                } else {
                    Color.rgb(105, 79, 163)
                }
            }
            AppPreferenceCache.ThemeBlue -> {
                if (darkMode) {
                    Color.rgb(175, 198, 255)
                } else {
                    Color.rgb(54, 92, 168)
                }
            }
            else -> {
                if (darkMode) {
                    Color.rgb(209, 188, 255)
                } else {
                    Color.rgb(105, 79, 163)
                }
            }
        }
    }

    protected open fun getOnPrimaryColor(darkMode: Boolean): Int {
        return when (AppPreferenceCache.curThemeValue) {
            AppPreferenceCache.ThemeDefault -> {
                if (darkMode) {
                    Color.rgb(58, 29, 113)
                } else {
                    Color.WHITE
                }
            }
            AppPreferenceCache.ThemeBlue -> {
                if (darkMode) {
                    Color.rgb(0, 45, 109)
                } else {
                    Color.WHITE
                }
            }
            else -> {
                if (darkMode) {
                    Color.rgb(58, 29, 113)
                } else {
                    Color.WHITE
                }
            }
        }
    }

    private fun immersive() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val sui = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = sui
        }
    }
}