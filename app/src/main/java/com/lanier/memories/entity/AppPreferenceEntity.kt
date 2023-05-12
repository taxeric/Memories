package com.lanier.memories.entity

/**
 * Author: Turtledove
 * Date  : on 2023/5/12
 * Desc  :
 */
data class AppPreferenceEntity(
    val defaultTheme: Int = 0,
) {

    companion object {
        const val ThemeDefault = 0
        const val ThemeBlue = 1
    }
}
