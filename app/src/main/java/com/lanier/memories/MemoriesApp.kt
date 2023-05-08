package com.lanier.memories

import android.app.Application

/**
 * Author: Turtledove
 * Date  : on 2023/5/8
 * Desc  :
 */
class MemoriesApp: Application() {

    override fun onCreate() {
        super.onCreate()
        MemoriesDatabase.initDb(this)
    }
}