package com.lanier.memories.glance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.lanier.memories.entity.MemoriesData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
object MemoriesGlanceDefinition2: GlanceStateDefinition<MemoriesData> {

    private const val DATASTORE_FILE = "memoriesDatastoreFile"
    private val Context.datastore by dataStore(DATASTORE_FILE, MemoriesSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<MemoriesData> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATASTORE_FILE)
    }

    object MemoriesSerializer: Serializer<MemoriesData> {
        override val defaultValue: MemoriesData
            get() = MemoriesData.default

        override suspend fun readFrom(input: InputStream): MemoriesData {
            return Json.decodeFromString(input.readBytes().decodeToString())
        }

        override suspend fun writeTo(t: MemoriesData, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(t).encodeToByteArray()
                )
            }
        }
    }
}