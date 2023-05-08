package com.lanier.memories

/**
 * Created by Eric
 * on 2023/5/8
 */
interface OnItemListener<T> {

    fun onItemClickListener(index: Int, item: T)
    fun onItemLongClickListener(index: Int, item: T)
}