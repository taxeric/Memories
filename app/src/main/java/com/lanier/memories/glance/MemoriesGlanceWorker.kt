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
import com.lanier.memories.entity.MemoriesData
import com.lanier.memories.repository.MemoriesDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        private val memoriesLocalData = mutableListOf<MemoriesData>()
        private var curIndex = -1

        private val glanceWidget = MemoriesGlanceWidget()

        @OptIn(DelicateCoroutinesApi::class)
        fun nextMemories(context: Context) {
            println(">>>> cache ${memoriesLocalData.size} $curIndex")
            if (memoriesLocalData.isEmpty()) {
                return
            }
            if (curIndex == -1) {
                return
            }
            if (curIndex != memoriesLocalData.size - 1) {
                curIndex ++
            } else {
                curIndex = 0
            }
            val next = memoriesLocalData[curIndex]
            println(">>>> next $next $curIndex")
            GlobalScope.launch {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(MemoriesGlanceWidget::class.java)
                println(">>>> ids -> ${glanceIds.size}")
                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(
                        context = context,
                        definition = MemoriesGlanceDefinition2,
                        glanceId = glanceId,
                        updateState = {
                            next
                        }
                    )
                    glanceWidget.update(context, glanceId)
                }
            }
        }

        fun syncMemories() {
        }

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
        if (memoriesData.isNotEmpty()) {
            memoriesLocalData.clear()
            memoriesLocalData.addAll(memoriesData)
            curIndex = 0
        }
        println(">>>> do work ${memoriesData.size} $curIndex")
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MemoriesGlanceWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = MemoriesGlanceDefinition2,
                glanceId = glanceId,
                updateState = {
                    memoriesLocalData[curIndex]
                }
            )
        }
        glanceWidget.updateAll(context)
        return Result.success()
    }
}