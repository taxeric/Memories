package com.lanier.memories.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lanier.memories.entity.AppPreferenceCache
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
        private const val P_K_BLAST_ANIM_ENABLE = "p_k_blast_anim_enable"

        val KeyThemeValue = intPreferencesKey(P_K_THEME)
        val KeyBlastAnim = booleanPreferencesKey(P_K_BLAST_ANIM_ENABLE)
    }

    val data = context.appDataStore.data

    val appPreferenceFlow = context.appDataStore
        .data
        .map {
            AppPreferenceCache.curThemeValue = it[KeyThemeValue]?: 0
            AppPreferenceCache.enabledBlastAnim = it[KeyBlastAnim]?: true
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

    fun isEnabledBlastAnim() = runBlocking { context.appDataStore.data.first() }[KeyBlastAnim]?: true

    suspend fun enabledBlastAnim(enabled: Boolean) {
        context.appDataStore
            .edit {
                it[KeyBlastAnim] = enabled
            }
    }
}