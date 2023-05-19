package com.lanier.memories.module

import android.content.Intent
import android.content.res.Configuration
import android.os.Process
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.material.appbar.MaterialToolbar
import com.lanier.memories.base.BaseAct
import com.lanier.memories.R
import com.lanier.memories.entity.AppPreferenceCache
import kotlinx.coroutines.runBlocking

/**
 * Author: Turtledove
 * Date  : on 2023/5/12
 * Desc  :
 */
class SettingAct(
    override val layoutId: Int = R.layout.activity_setting
) : BaseAct() {

    private var darkMode = false

    private val mToolbar by lazy {
        findViewById<MaterialToolbar>(R.id.toolbar)
    }

    override fun initViews() {
        darkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        mToolbar.run {
            setBackgroundColor(getPrimaryColor(darkMode))
            setTitle(R.string.preferences)
            setTitleTextColor(getOnPrimaryColor(darkMode))
            setNavigationIcon(
                if (darkMode) R.drawable.baseline_arrow_back_24_dark
                else R.drawable.baseline_arrow_back_24_light
            )
        }
        setSupportActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener {
            finish()
        }

        findViewById<ComposeView>(R.id.composeSetTheme)
            .setContent {
                SetTheme(
                    helper = AppPreferenceCache
                ) {
                    if (it != -1) {
                        runBlocking { preferenceDataStore.setTheme(it) }
                        restartApp()
                    }
                }
            }

        val blastAnimEnabled = preferenceDataStore.isEnabledBlastAnim()
        findViewById<ComposeView>(R.id.composeEnableBlastAnim)
            .setContent {
                BlastAnimEnabled(
                    checked = blastAnimEnabled,
                    onChanged = {
                        runBlocking { preferenceDataStore.enabledBlastAnim(it) }
                    }
                )
            }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Process.killProcess(Process.myPid())
    }
}

@Composable
private fun SetTheme(
    helper: AppPreferenceCache,
    onChoose: (Int) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .clickable(
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            ) { showDialog = !showDialog }
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(4f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "设定主题",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                    color = MaterialTheme.colorScheme.onSurface.applyOpacity(true)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Spacer(
                    modifier = Modifier
                        .background(
                            color = when (helper.curThemeValue) {
                                AppPreferenceCache.ThemeDefault -> Color(0xFF694FA3)
                                AppPreferenceCache.ThemeBlue -> Color(0xFF365CA8)
                                else -> Color(0xFF694FA3)
                            },
                            shape = CircleShape
                        )
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
    if (showDialog) {
        SetThemeDialog(
            curTheme = helper.curThemeValue,
            onClose = {
                showDialog = !showDialog
                onChoose.invoke(it)
            }
        )
    }
}

@Composable
private fun BlastAnimEnabled(
    checked: Boolean,
    onChanged: (Boolean) -> Unit
) {
    var mCheck by remember {
        mutableStateOf(checked)
    }
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "新增Memories启用动画",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                    color = MaterialTheme.colorScheme.onSurface.applyOpacity(true)
                )
            }
            Switch(
                checked = mCheck,
                onCheckedChange = {
                    mCheck = it
                    onChanged.invoke(it)
                }
            )
        }
    }
}

@Composable
private fun SetThemeDialog(
    curTheme: Int,
    onClose: (Int) -> Unit
) {
    Dialog(
        onDismissRequest = { onClose.invoke(-1) },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
            Text(
                text = "选择主题",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(
                modifier = Modifier
                    .height(20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                SetThemeDialogColor(
                    choice = curTheme == AppPreferenceCache.ThemeDefault,
                    color = Color(0xFF694FA3),
                ) {
                    onClose.invoke(0)
                }
                Spacer(modifier = Modifier.width(8.dp))
                SetThemeDialogColor(
                    choice = curTheme == AppPreferenceCache.ThemeBlue,
                    color = Color(0xFF365CA8)
                ) {
                    onClose.invoke(1)
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
private fun SetThemeDialogColor(
    choice: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                color = color,
            )
            .clickable(
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick.invoke() }
    ) {
        if (choice) {
            Image(
                painter = painterResource(id = R.drawable.baseline_check_24),
                contentDescription = "",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

private fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}
