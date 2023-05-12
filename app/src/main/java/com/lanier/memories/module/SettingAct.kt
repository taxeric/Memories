package com.lanier.memories.module

import android.content.res.Configuration
import com.google.android.material.appbar.MaterialToolbar
import com.lanier.memories.base.BaseAct
import com.lanier.memories.R

/**
 * Author: Turtledove
 * Date  : on 2023/5/12
 * Desc  :
 */
class SettingAct(
    override val layoutId: Int = R.layout.activity_setting
) : BaseAct() {

    private var darkMode = false

    private val mToolbar by lazy {
        findViewById<MaterialToolbar>(R.id.toolbar)
    }

    override fun initViews() {
        darkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        mToolbar.run {
            setBackgroundColor(getPrimaryColor(darkMode))
            setTitle(R.string.preferences)
            setTitleTextColor(getOnPrimaryColor(darkMode))
            setNavigationIcon(
                if (darkMode) R.drawable.baseline_arrow_back_24_dark
                else R.drawable.baseline_arrow_back_24_light
            )
        }
        setSupportActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
