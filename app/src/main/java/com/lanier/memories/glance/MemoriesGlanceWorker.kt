package com.lanier.memories.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.lanier.memories.repository.MemoriesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
class MemoriesGlanceWorker(
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = MemoriesGlanceWorker::class.java.name

        fun enqueue(context: Context) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MemoriesGlanceWorker>(
                Duration.ofMinutes(1)
            )
            val workPolicy = ExistingPeriodicWorkPolicy.KEEP
            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        MemoriesDatabase.initDb(context.applicationContext)
        val memoriesData = withContext(Dispatchers.IO) {
            MemoriesDatabase.db.memoriesDao()
                .getMemoriesByShowInGlance()
        }
        println(">>>> ${memoriesData.size}")
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