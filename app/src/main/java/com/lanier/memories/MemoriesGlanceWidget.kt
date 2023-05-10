package com.lanier.memories

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.BitmapImageProvider
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.background
import androidx.glance.color.dynamicThemeColorProviders
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
class MemoriesGlanceWidget: GlanceAppWidget() {

    @Composable
    override fun Content() {
        val memoriesDataList = currentState<List<MemoriesData>>()
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    dynamicThemeColorProviders().background
//                    if (isSystemInDarkTheme()) Color(0xFFECDDFE) else Color(0xFF7F5FA5)
                )
        ) {
            if (memoriesDataList.isNotEmpty()) {
                val memoriesData = memoriesDataList[0]
                val bitmap = obtainBitmap(Uri.parse(memoriesData.path))
                Image(
                    provider = BitmapImageProvider(bitmap = bitmap),
                    contentDescription = "",
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(50.dp)
                )
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = memoriesData.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                    Text(
                        text = memoriesData.desc,
                        style = TextStyle(
                            fontSize = 12.sp,
                        ),
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            } else {
                Text(text = "点击选择")
            }
        }
    }

    @Composable
    private fun obtainBitmap(uri: Uri): Bitmap {
        val context = LocalContext.current
        return remember {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    override val stateDefinition: GlanceStateDefinition<*>
        get() = MemoriesGlanceDefinition
}