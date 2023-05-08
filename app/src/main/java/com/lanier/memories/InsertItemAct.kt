package com.lanier.memories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.imageview.ShapeableImageView

class InsertItemAct : AppCompatActivity() {

    private val ivPic by lazy {
        findViewById<ShapeableImageView>(R.id.ivPic)
    }

    private val selectPicResult = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.let { uri ->
            ivPic.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_item)

        ivPic.setOnClickListener {
            selectPicResult.launch("image/*")
        }
    }
}