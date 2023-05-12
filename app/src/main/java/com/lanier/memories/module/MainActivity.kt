package com.lanier.memories.module

import android.animation.ValueAnimator
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.lanier.memories.MemoriesItemFlow
import com.lanier.memories.entity.OnItemListener
import com.lanier.memories.R
import com.lanier.memories.RefreshItemFlow
import com.lanier.memories.base.BaseAct
import com.lanier.memories.entity.MemoriesData
import com.lanier.memories.invisible
import com.lanier.memories.smoothScrollToPosition2
import com.lanier.memories.start
import com.lanier.memories.visible
import kotlinx.coroutines.launch

class MainActivity(
    override val layoutId: Int = R.layout.activity_main
) : BaseAct() {

    private val vm by viewModels<MainVM>()

    private val rv by lazy {
        findViewById<RecyclerView>(R.id.recyclerView)
    }
    private val refreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
    }
    private lateinit var mAdapter: MA
    private lateinit var mLayoutManager: GridLayoutManager

    private val fabScrollToTop by lazy {
        findViewById<FloatingActionButton>(R.id.fabScrollToTop)
    }
    private lateinit var fabScrollEnterAnim: ValueAnimator
    private lateinit var fabScrollExitAnim: ValueAnimator

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

    override fun initViews() {
        mAdapter = MA(rv).apply {
            listener = object : OnItemListener<MemoriesData> {
                override fun onItemClickListener(index: Int, item: MemoriesData) {
                    toMemoriesDetails(index, item)
                }

                override fun onItemLongClickListener(index: Int, item: MemoriesData) {
                    deleteMemories(index, item)
                }
            }
        }
        mLayoutManager = GridLayoutManager(this@MainActivity, 2)
        rv.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
            addOnScrollListener(rvScrollListener)
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

        fabScrollEnterAnim = ValueAnimator.ofFloat(200f, 0f).apply {
            addUpdateListener { listener ->
                fabScrollToTop.translationX = listener.animatedValue as Float
            }
        }
        fabScrollExitAnim = ValueAnimator.ofFloat(0f, 200f).apply {
            addUpdateListener { listener ->
                val value = listener.animatedValue as Float
                fabScrollToTop.translationX = value
                if (value == 200f) {
                    fabScrollToTop.invisible()
                }
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAdd)
            .setOnClickListener {
                start<InsertItemAct> {  }
            }
        fabScrollToTop
            .setOnClickListener {
                if (mAdapter.modelCount != 0) {
                    rv.smoothScrollToPosition2(0, mLayoutManager)
                }
            }

        lifecycleScope
            .launch {
                RefreshItemFlow.collect {
                    refreshFunc.invoke(false)
                }
            }

        refreshFunc.invoke(true)
    }

    private val rvScrollListener = object : RecyclerView.OnScrollListener() {

        private var slideDown = true

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (slideDown && fabScrollToTop.visibility == View.INVISIBLE && mAdapter.modelCount != 0) {
                    fabScrollEnterAnim.start()
                    fabScrollToTop.visible()
                } else if (!slideDown && mLayoutManager.findFirstVisibleItemPosition() == 0) {
                    fabScrollExitAnim.start()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            slideDown = dy > 0
        }
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

    private fun toMemoriesDetails(index: Int, data: MemoriesData) {
        val uri = Uri.parse(data.path)
        DocumentFile.fromSingleUri(this, uri)?.let {
            if (!it.exists()) {
                data.uriValid = false
                mAdapter.notifyItemChanged(index)
            }
        }
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

    val modelCount: Int
        get() = _data.size

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
        if (memoriesData.uriValid) {
            val uri = Uri.parse(memoriesData.path)
            iv.setImageURI(uri)
        } else {
            iv.setImageResource(R.drawable.ic_not_found)
        }
    }
}