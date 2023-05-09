package com.lanier.memories

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class InsertItemAct : AppCompatActivity() {

    private val ivPic by lazy {
        findViewById<ShapeableImageView>(R.id.ivPic)
    }

    private val pictureFlow = MutableStateFlow<Uri?>(null)

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
                    lifecycleScope.launch(Dispatchers.IO) {
                        val data = MemoriesData(
                            path = uri.toString(),
                            name = "名字",
                            desc = "描述"
                        )
                        MemoriesRoomHelper
                            .insertMemories(
                                data
                            )
                        RefreshItemFlow.tryEmit(RefreshItemFlow.value + 1)
                        finish()
                    }
                }?: Toast.makeText(this, "无效", Toast.LENGTH_SHORT).show()
            }
    }
}