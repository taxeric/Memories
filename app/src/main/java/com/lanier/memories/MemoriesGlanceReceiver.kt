package com.lanier.memories

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
class MemoriesGlanceReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MemoriesGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        println(">>>> enabled")
        MemoriesGlanceWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        println(">>>> disabled")
        MemoriesGlanceWorker.cancel(context)
    }
}