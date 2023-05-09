package com.lanier.memories

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Author: Turtledove
 * Date  : on 2023/5/9
 * Desc  :
 */
val RefreshItemFlow = MutableStateFlow(0)
val MemoriesItemFlow = MutableSharedFlow<MemoriesData>(replay = 1)
