package com.lanier.memories

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var mAdapter: MA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = MA(rv).apply {
            listener = object : OnItemListener<MemoriesData> {
                override fun onItemClickListener(index: Int, item: MemoriesData) {
                    toMemoriesDetails(item)
                }

                override fun onItemLongClickListener(index: Int, item: MemoriesData) {
                    deleteMemories(index, item)
                }
            }
        }
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

    private fun deleteMemories(index: Int, data: MemoriesData) {
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                lifecycleScope
                    .launch {
                        withContext(Dispatchers.IO) {
                            MemoriesRoomHelper.deleteMemories(data)
                        }
                        withContext(Dispatchers.Main) {
                            mAdapter
                                .remove(index)
                                .notifyItemRemoved(index)
                        }
                    }
            }
        }
        AlertDialog.Builder(this)
            .setTitle("delete?")
            .setPositiveButton("确定", listener)
            .setNegativeButton("取消", listener)
            .show()
    }

    private fun toMemoriesDetails(data: MemoriesData) {
    }
}

class MA(
    private val recyclerView: RecyclerView
): RecyclerView.Adapter<VH>(),
    View.OnClickListener,
    View.OnLongClickListener {

    var listener: OnItemListener<MemoriesData>? = null

    private val _data = mutableListOf<MemoriesData>()
    var data: List<MemoriesData>
        get() = _data
        set(value) {
            _data.clear()
            _data.addAll(value)
            notifyDataSetChanged()
        }

    fun remove(index: Int): MA {
        if (_data.isEmpty()) {
            return this
        }
        if (index < 0 || index > _data.size - 1) {
            return this
        }
        _data.removeAt(index)
        return this
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
        holder.itemView
            .setOnClickListener(this)
        holder.itemView
            .setOnLongClickListener(this)
    }

    override fun onClick(v: View) {
        val index = recyclerView.getChildAdapterPosition(v)
        listener?.onItemClickListener(index, _data[index])
    }

    override fun onLongClick(v: View): Boolean {
        val index = recyclerView.getChildAdapterPosition(v)
        listener?.onItemLongClickListener(index, _data[index])
        return true
    }
}

class VH(view: View): RecyclerView.ViewHolder(view) {

    val iv = view.findViewById<ShapeableImageView>(R.id.ivPic)
}