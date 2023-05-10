package com.lanier.memories

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.color.dynamicThemeColorProviders
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
class MemoriesGlanceWidget: GlanceAppWidget() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val list = remember {
            mutableStateListOf<MemoriesData>()
        }
        val m = currentState<MemoriesData>()
//        updateAppWidgetState()
        LaunchedEffect(key1 = Unit) {
            MemoriesDatabase.initDb(context.applicationContext)
            list.clear()
            val memoriesData = withContext(Dispatchers.IO) {
                MemoriesDatabase.db.memoriesDao()
                    .getMemoriesByShowInGlance()
            }
            list.addAll(memoriesData)
        }
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    dynamicThemeColorProviders().background
//                    if (isSystemInDarkTheme()) Color(0xFFECDDFE) else Color(0xFF7F5FA5)
                )
        ) {
            if (list.isNotEmpty()) {
                val memoriesData = list[0]
                Image(provider = ImageProvider(uri = Uri.parse(memoriesData.path)), contentDescription = "")
            } else {
                Text(text = "")
            }
        }
    }
}