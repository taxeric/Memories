package com.lanier.memories

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.BitmapImageProvider
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.background
import androidx.glance.color.dynamicThemeColorProviders
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
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
                Column(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(250.dp)
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp
                        )
                ) {
                    Text(
                        text = memoriesData.name,
                        style = TextStyle(
                            fontSize = 18.sp,
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
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
                Column(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(Color.LightGray)
                ) {}
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        provider = BitmapImageProvider(bitmap = bitmap),
                        contentDescription = "",
                        modifier = GlanceModifier
                            .size(50.dp)
                    )
                    Spacer(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                    Button(
                        text = "→",
                        style = TextStyle(
                            textAlign = TextAlign.Center
                        ),
                        onClick = actionStartActivity(
                            MemoriesDetailsAct::class.java,
                            actionParametersOf(
                                ActionParameters.Key<Int>("M_ID") to memoriesData.id
                            )
                        ),
                        modifier = GlanceModifier
                            .width(80.dp)
                    )
                }
            } else {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        text = "点击选择",
                        onClick = actionStartActivity(
                            MainActivity::class.java
                        ),
                        modifier = GlanceModifier
                    )
                }
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