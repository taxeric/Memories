package com.lanier.memories.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.lanier.memories.datastore.AppPreference
import com.lanier.memories.entity.AppPreferenceEntity
import com.lanier.memories.R
import com.lanier.memories.helper.ThemeHelper

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
        if (ThemeHelper.curThemeValue == -1) {
            ThemeHelper.curThemeValue = preferenceDataStore.getTheme()
            ThemeHelper.curTheme = when (ThemeHelper.curThemeValue) {
                AppPreferenceEntity.ThemeDefault -> R.style.Base_Theme_Memories
                AppPreferenceEntity.ThemeBlue -> R.style.Base_Theme_Blue
                else -> R.style.Base_Theme_Memories
            }
            if (ThemeHelper.curThemeValue == -1) {
                ThemeHelper.curThemeValue = 0
            }
        }
        super.onCreate(savedInstanceState)
        setTheme(ThemeHelper.curTheme)
        setContentView(layoutId)
        if (immersiveStatusBar) {
            immersive()
        }
        initViews()
    }

    abstract val layoutId: Int
    protected open val immersiveStatusBar: Boolean = false
    protected open fun initViews(){}

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