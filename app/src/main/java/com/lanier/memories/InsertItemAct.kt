package com.lanier.memories

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsertItemAct : AppCompatActivity() {

    private val ivPic by lazy {
        findViewById<ShapeableImageView>(R.id.ivPic)
    }

    private val pictureFlow = MutableStateFlow<Uri?>(null)
    private var editName: String = ""
    private var editDesc: String = ""

    private val selectPicResult = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let { uri ->
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pictureFlow.tryEmit(uri)
            ivPic.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_item)

        ivPic.setOnClickListener {
            selectPicResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        findViewById<Button>(R.id.btnAdd)
            .setOnClickListener {
                val uri = pictureFlow.value
                uri?.let {
                    lifecycleScope.launch {
                        val data = MemoriesData(
                            path = uri.toString(),
                            name = editName.ifEmpty { "default" },
                            desc = editDesc.ifEmpty { "default" },
                            time = System.currentTimeMillis(),
                        )
                        withContext(Dispatchers.IO) {
                            MemoriesRoomHelper
                                .insertMemories(
                                    data
                                )
                        }
                        RefreshItemFlow.tryEmit(RefreshItemFlow.value + 1)
                        try {
                            val ams = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                            val tasks = ams.appTasks
                            if (tasks.size == 1) {
                                val task = tasks[0]
                                if (task.taskInfo.numActivities == 1) {
                                    start<MainActivity> {  }
                                }
                            }
                        } finally {
                            finish()
                        }
                    }
                }?: Toast.makeText(this, "无效", Toast.LENGTH_SHORT).show()
            }

        findViewById<ComposeView>(R.id.composeEditName)
            .setContent {
                InsertItemSingleEditView(
                    hint = stringResource(id = R.string.edit_name),
                    onValueChanged = {
                        editName = it
                    }
                )
            }
        findViewById<ComposeView>(R.id.composeEditDesc)
            .setContent {
                InsertItemSingleEditView(
                    hint = stringResource(id = R.string.edit_desc),
                    onValueChanged = {
                        editDesc = it
                    }
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InsertItemSingleEditView(
    modifier: Modifier = Modifier,
    hint: String = "",
    onValueChanged: (String) -> Unit,
) {
    var strKey by remember { mutableStateOf("") }
    val dark = isSystemInDarkTheme()
    OutlinedTextField(
        value = strKey,
        onValueChange = {
            strKey = it
            onValueChanged.invoke(it)
        },
        label = {
            Text(
                text = hint,
                color = if (dark) Color.LightGray else Color.Gray,
                fontSize = 12.sp,
            )
        },
        singleLine = true,
        leadingIcon = @Composable {
            Icon(
                painter = painterResource(id = R.drawable.baseline_favorite_border_24),
                contentDescription = ""
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = if (dark) Color.LightGray else Color.Black,
            cursorColor = if (dark) Color.LightGray else Color.Black,
        ),
        modifier = modifier
    )
}