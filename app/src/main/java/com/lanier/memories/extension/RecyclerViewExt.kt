package com.lanier.memories

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Author: Turtledove
 * Date  : on 2023/5/10
 * Desc  :
 */
fun RecyclerView.smoothScrollToPosition2(position: Int, layoutManager: LinearLayoutManager) {
    val firstPos: Int = layoutManager.findFirstVisibleItemPosition()
    val lastPos: Int = layoutManager.findLastVisibleItemPosition()
    if (position in (firstPos + 1) until lastPos) {
        val childAt: View? = layoutManager.findViewByPosition(position)
        val top = childAt?.top ?: 0
        smoothScrollBy(0, top)
    } else {
        smoothScrollToPosition(position)
    }
}
