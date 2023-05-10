package com.lanier.memories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
object MemoriesGlanceDefinition: GlanceStateDefinition<List<MemoriesData>> {

    private const val DATASTORE_FILE = "memoriesDatastoreFile"
    private val Context.datastore by dataStore(DATASTORE_FILE, MemoriesSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<List<MemoriesData>> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATASTORE_FILE)
    }

    object MemoriesSerializer: Serializer<List<MemoriesData>> {
        override val defaultValue: List<MemoriesData>
            get() = emptyList()

        override suspend fun readFrom(input: InputStream): List<MemoriesData> {
            TODO("Not yet implemented")
        }

        override suspend fun writeTo(t: List<MemoriesData>, output: OutputStream) {
            TODO("Not yet implemented")
        }
    }
}