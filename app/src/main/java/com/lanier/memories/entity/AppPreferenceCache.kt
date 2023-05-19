package com.lanier.memories.entity

import com.lanier.memories.R

/**
 * Author: Turtledove
 * Date  : on 2023/5/12
 * Desc  :
 */
object AppPreferenceCache{

    const val ThemeDefault = 0
    const val ThemeBlue = 1

    /**
     * 当前主题
     */
    var curThemeValue = -1
    var curTheme = R.style.Theme_Memories

    /**
     * 是否启用爆炸动画
     */
    var enabledBlastAnim = true
}
