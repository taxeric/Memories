package com.lanier.memories.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        println(">>>> on update")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.action?.let {
            println(">>>> on received $it")
            when (it) {
                MemoriesGlanceAction.NEXT.name -> {
                    println(">>>> 执行next work")
                    MemoriesGlanceWorker.nextMemories(context)
                }
                else -> {}
            }
        }
    }

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