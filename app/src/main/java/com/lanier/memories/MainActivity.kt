package com.lanier.memories

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val rv by lazy {
        findViewById<RecyclerView>(R.id.recyclerView)
    }
    private val mAdapter by lazy {
        MA()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }

        findViewById<FloatingActionButton>(R.id.floatActionButton)
            .setOnClickListener {
                start<InsertItemAct> {  }
            }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope
            .launch {
                val list = withContext(Dispatchers.IO) {
                    MemoriesRoomHelper.getAllMemories()
                }
                mAdapter
                    .data = list
            }
    }
}

class MA: RecyclerView.Adapter<VH>() {

    private val _data = mutableListOf<MemoriesData>()
    var data: List<MemoriesData>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rv, parent, false)
        )
    }

    override fun getItemCount() = _data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val uri = Uri.parse(_data[position].path)
        holder.iv.setImageURI(uri)
    }
}

class VH(view: View): RecyclerView.ViewHolder(view) {

    val iv = view.findViewById<ShapeableImageView>(R.id.ivPic)
}