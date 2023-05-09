package com.lanier.memories

import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val vm by viewModels<MainVM>()

    private val rv by lazy {
        findViewById<RecyclerView>(R.id.recyclerView)
    }
    private val refreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
    }
    private lateinit var mAdapter: MA

    private val refreshFunc = fun (showLoading: Boolean) {
        if (showLoading) {
            refreshLayout.isRefreshing = true
        }
        vm.getAllMemories { data ->
            mAdapter.data = data
            if (showLoading) {
                refreshLayout.isRefreshing = false
            }
        }
    }

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
        refreshLayout
            .apply {
                setColorSchemeColors(
                    Color.RED,
                    Color.GREEN,
                    Color.BLUE,
                )
                setOnRefreshListener {
                    refreshFunc.invoke(true)
                }
            }


        findViewById<FloatingActionButton>(R.id.floatActionButton)
            .setOnClickListener {
                start<InsertItemAct> {  }
            }

        lifecycleScope
            .launch {
                RefreshItemFlow.collect {
                    refreshFunc.invoke(false)
                }
            }

        refreshFunc.invoke(true)
    }

    private fun deleteMemories(index: Int, data: MemoriesData) {
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                vm.deleteMemories(data) {
                    mAdapter
                        .remove(index)
                        .notifyItemRemoved(index)
                }
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Delete?")
            .setPositiveButton("确定", listener)
            .setNegativeButton("取消", listener)
            .show()
    }

    private fun toMemoriesDetails(data: MemoriesData) {
        MemoriesItemFlow.tryEmit(data)
        start<MemoriesDetailsAct> {  }
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
        holder.bind(_data[position])
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

    private val iv = view.findViewById<ShapeableImageView>(R.id.ivPic)

    fun bind(memoriesData: MemoriesData) {
        val uri = Uri.parse(memoriesData.path)
        iv.setImageURI(uri)
    }
}