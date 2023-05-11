package com.lanier.memories

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch


/**
 * Author: Turtledove
 * Date  : on 2023/5/9
 * Desc  :
 */
class MemoriesDetailsAct: AppCompatActivity() {

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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        immersive()

        println(">>>> $intent")
        mMemoriesDataId = intent.getIntExtra("M_ID", -1)

        lifecycleScope
            .launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    if (mMemoriesDataId == -1) {
                        MemoriesItemFlow.collect {
                            toolbar.title = it.name
                            ivPic.setImageURI(Uri.parse(it.path))
                            tvTime.text = it.time.toTime()
                            tvDesc.text = it.desc
                        }
                    } else {
                        val memoriesData = MemoriesRoomHelper.getMemoriesById(mMemoriesDataId)
                        toolbar.title = memoriesData.name
                        ivPic.setImageURI(Uri.parse(memoriesData.path))
                        tvTime.text = memoriesData.time.toTime()
                        tvDesc.text = memoriesData.desc
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

    private fun immersive() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val sui = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = sui
        }
    }
}