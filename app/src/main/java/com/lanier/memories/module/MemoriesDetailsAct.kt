package com.lanier.memories.module

import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.memories.MemoriesItemFlow
import com.lanier.memories.helper.MemoriesRoomHelper
import com.lanier.memories.R
import com.lanier.memories.base.BaseAct
import com.lanier.memories.entity.MemoriesData
import com.lanier.memories.toTime
import kotlinx.coroutines.launch

/**
 * Author: Turtledove
 * Date  : on 2023/5/9
 * Desc  :
 */
class MemoriesDetailsAct(
    override val layoutId: Int = R.layout.activity_details,
) : BaseAct() {

    private val toolbar by lazy {
        findViewById<MaterialToolbar>(R.id.toolbar)
    }
    private val ivPic by lazy {
        findViewById<ShapeableImageView>(R.id.ivPic)
    }
    private val tvTime by lazy {
        findViewById<TextView>(R.id.tvTime)
    }
    private val tvDesc by lazy {
        findViewById<TextView>(R.id.tvDesc)
    }

    private var mMemoriesDataId = -1

    override val immersiveStatusBar = true

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initViews() {
        println(">>>> $intent")
        mMemoriesDataId = intent.getIntExtra("M_ID", -1)

        lifecycleScope
            .launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    if (mMemoriesDataId == -1) {
                        MemoriesItemFlow.collect {
                            bindMemories(it)
                        }
                    } else {
                        val memoriesData = MemoriesRoomHelper.getMemoriesById(mMemoriesDataId)
                        bindMemories(memoriesData)
                    }
                }
            }

        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val w2 = window.decorView.width
                val params = ivPic.layoutParams
                params.height = w2
                ivPic.layoutParams = params
            }
        })
    }

    private fun bindMemories(memoriesData: MemoriesData) {
        val uri = Uri.parse(memoriesData.path)
        toolbar.title = memoriesData.name
        tvTime.text = memoriesData.time.toTime()
        tvDesc.text = memoriesData.desc
        DocumentFile.fromSingleUri(this, uri)?.let {
            if (it.exists()) {
                ivPic.setImageURI(uri)
            } else {
                ivPic.setImageResource(R.drawable.ic_not_found)
            }
        }
    }
}