package com.lanier.memories

import android.app.Application
import com.lanier.memories.repository.MemoriesDatabase

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