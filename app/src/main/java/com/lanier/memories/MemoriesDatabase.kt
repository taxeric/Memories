package com.lanier.memories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Author: Turtledove
 * Date  : on 2023/5/8
 * Desc  :
 */
@Database(
    entities = [
        MemoriesData::class,
    ],
    version = 1,
)
abstract class MemoriesDatabase: RoomDatabase() {

    abstract fun memoriesDao(): MemoriesDao

    companion object {

        private const val DATABASE_NAME = "memories.db"

        @Volatile
        lateinit var db: MemoriesDatabase

        fun initDb(context: Context) {
            if (::db.isInitialized) {
                return
            }
            synchronized(this) {
                val instance = Room
                    .databaseBuilder(context, MemoriesDatabase::class.java, DATABASE_NAME)
                    .build()
                db = instance
            }
        }
    }
}