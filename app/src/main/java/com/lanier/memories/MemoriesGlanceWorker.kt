package com.lanier.memories

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
class MemoriesGlanceWorker(
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        MemoriesDatabase.initDb(context.applicationContext)
        val memoriesData = withContext(Dispatchers.IO) {
            MemoriesDatabase.db.memoriesDao()
                .getMemoriesByShowInGlance()
        }
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MemoriesGlanceWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = MemoriesGlanceDefinition,
                glanceId = glanceId,
                updateState = {
                    memoriesData
                }
            )
        }
        MemoriesGlanceWidget().updateAll(context)
        return Result.success()
    }
}