package com.lanier.memories.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lanier.memories.entity.AppPreferenceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Author: Turtledove
 * Date  : on 2023/5/12
 * Desc  :
 */
private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "local_info"
)

class AppPreference(
    private val context: Context
) {

    companion object {

        private const val P_K_THEME = "p_k_theme"

        val KeyThemeValue = intPreferencesKey(P_K_THEME)
    }

    val data = context.appDataStore.data

    val appPreferenceFlow = context.appDataStore
        .data
        .map {
            AppPreferenceEntity(
                defaultTheme = it[KeyThemeValue]?: 0
            )
        }

    fun getTheme(): Int {
        return runBlocking { context.appDataStore.data.first() }[KeyThemeValue]?: -1
    }

    suspend fun setTheme(value: Int = 0) {
        context.appDataStore
            .edit {
                it[KeyThemeValue] = value
            }
    }
}