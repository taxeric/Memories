package com.lanier.memories

import android.content.Context
import android.content.Intent

/**
 * Create by Eric
 * on 2023/5/8
 */
inline fun <reified Z> Context.start(
    crossinline action: Intent.() -> Unit = {}
) {
    startActivity(
        Intent(this, Z::class.java).apply {
            action.invoke(this)
        }
    )
}
