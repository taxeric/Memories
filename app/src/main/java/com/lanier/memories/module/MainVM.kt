package com.lanier.memories.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanier.memories.helper.MemoriesRoomHelper
import com.lanier.memories.entity.MemoriesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Author: Turtledove
 * Date  : on 2023/5/9
 * Desc  :
 */
class MainVM: ViewModel() {

    fun getAllMemories(
        callback: (List<MemoriesData>) -> Unit
    ) {
        viewModelScope
            .launch {
                val list = withContext(Dispatchers.IO) {
                    MemoriesRoomHelper.getAllMemories()
                }
                withContext(Dispatchers.Main) {
                    callback.invoke(list)
                }
            }
    }

    fun deleteMemories(
        memoriesData: MemoriesData,
        callback: () -> Unit
    ) {
        viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    MemoriesRoomHelper.deleteMemories(memoriesData)
                }
                withContext(Dispatchers.Main) {
                    callback.invoke()
                }
            }
    }
}