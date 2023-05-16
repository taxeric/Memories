package com.lanier.memories.module

import android.app.ActivityManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.ContentFrameLayout
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
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.lanier.memories.helper.MemoriesRoomHelper
import com.lanier.memories.R
import com.lanier.memories.RefreshItemFlow
import com.lanier.memories.base.BaseAct
import com.lanier.memories.entity.MemoriesData
import com.lanier.memories.start
import com.lanier.memories.widget.ParticleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class InsertItemAct(
    override val layoutId: Int = R.layout.activity_insert_item
) : BaseAct() {

    private val ivPic by lazy {
        findViewById<ShapeableImageView>(R.id.ivPic)
    }
    private val msCrop by lazy {
        findViewById<MaterialSwitch>(R.id.msCrop)
    }
    private val msShowInGlance by lazy {
        findViewById<MaterialSwitch>(R.id.msShowInGlance)
    }
    private val msTagFavourite by lazy {
        findViewById<MaterialSwitch>(R.id.msTagFavorite)
    }
    private val topView by lazy {
        findViewById<ContentFrameLayout>(android.R.id.content)
    }

    private val pictureFlow = MutableStateFlow<Uri?>(null)
    private var editName: String = ""
    private var editDesc: String = ""

    private val selectPicResult = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let { uri ->
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            if (!msCrop.isChecked) {
                bindUri(uri)
            } else {
                cropPictureUri(uri)
            }
        }
    }
    private val cropPicResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { actionResult ->
        actionResult?.data?.data?.let { uri ->
            bindUri(uri)
        }
    }

    override fun initViews() {
        ParticleHelper.initRes(
            resources,
            ids = intArrayOf(
                R.drawable.ic_xls,
                R.drawable.ic_mgdd,
                R.drawable.ic_lm,
                R.drawable.ic_xls,
                R.drawable.ic_mgdd,
                R.drawable.ic_lm,
                R.drawable.ic_xls,
                R.drawable.ic_mgdd,
                R.drawable.ic_lm,
                R.drawable.ic_xls,
                R.drawable.ic_mgdd,
                R.drawable.ic_lm,
            ),
            topView
        )
        ivPic.setOnClickListener {
            selectPicResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        findViewById<Button>(R.id.btnAdd)
            .setOnClickListener {
                ParticleHelper.start(it, topView)
//                apply()
            }

        findViewById<ComposeView>(R.id.composeEditName)
            .setContent {
                InsertItemSingleEditView(
                    hint = stringResource(id = R.string.edit_name),
                    singleLine = true,
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

    private fun apply() {
        val uri = pictureFlow.value
        uri?.let {
            lifecycleScope.launch {
                val data = MemoriesData(
                    path = uri.toString(),
                    name = editName.ifEmpty { "default" },
                    desc = editDesc.ifEmpty { "default" },
                    time = System.currentTimeMillis(),
                    favourite = msTagFavourite.isChecked,
                    showInGlance = msShowInGlance.isChecked
                )
                withContext(Dispatchers.IO) {
                    MemoriesRoomHelper.insertMemories(
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
        }?: Snackbar
            .make(findViewById(android.R.id.content), "invalid", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun bindUri(uri: Uri) {
        pictureFlow.tryEmit(uri)
        ivPic.setImageURI(uri)
    }

    private fun cropPictureUri(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "image/*")
        //可裁剪
        intent.putExtra("crop", "true")
        //比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("circleCrop", true);
        //缩放
        intent.putExtra("scale", false)
        intent.putExtra("outputX", 400)
        intent.putExtra("outputY", 400)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, obtainMediaUri())
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("return-data", false)
        cropPicResult.launch(intent)
    }

    private fun obtainMediaUri(): Uri {
        val parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path +
                File.separator + "Memories" + File.separator
        val filename = "memories_${System.currentTimeMillis()}.jpeg"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, filename)
        contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.Images.ImageColumns.DATE_ADDED, System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                MediaStore.Images.ImageColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + "Memories"
            )
        } else {
            contentValues.put(MediaStore.Images.ImageColumns.DATA, parentPath + filename)
        }
        val newUri =  contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        return newUri!!
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InsertItemSingleEditView(
    modifier: Modifier = Modifier,
    hint: String = "",
    singleLine: Boolean = false,
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
        singleLine = singleLine,
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